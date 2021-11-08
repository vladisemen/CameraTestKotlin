package com.example.cameratest.ui.main

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cameratest.R
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhotoPath: String = ""
    lateinit var PhotoPath: Uri
    private val KEY_INDEX = "index"
    val REQUEST_CODE = 100


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putBoolean(IS_EDITING_KEY, isEditing)
        outState.putString(KEY_INDEX, textView1.text as String?)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val s = savedInstanceState?.getString(KEY_INDEX, "1") ?: "1"
        button_show_photo.setOnClickListener {
            if (currentPhotoPath !== ""){
                imageView.setImageURI(this.PhotoPath)
            }
        }
        button_delete.setOnClickListener {
            deletePicture()
        }
        button_open_camera.setOnClickListener {
            dispatchTakePictureIntent()
            galleryAddPic()
        }
        button_openG.setOnClickListener {
            openGalleryForImage()
        }
        button_post.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main){
                val nameStr = withContext(Dispatchers.Default) {
                    post()
                }
                val duration = Toast.LENGTH_SHORT
                Toast.makeText(context, nameStr, duration).show()
                textView1.text = nameStr
            }

        }
        //dispatchTakePictureIntent()
        //galleryAddPic()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            imageView.setImageURI(data?.data) // handle chosen image
        }
    }
    private fun dispatchTakePictureIntent() {
        val activity = activity
        if(activity != null) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created

                val cont = activity?.applicationContext
                if (cont != null) {
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                cont,
                                "com.example.android.fileprovider",
                                it
                        )
                        PhotoPath = photoURI
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        print("Это путь $photoURI")
                    }
                }

            }
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        //val storageDir1: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
            //"JPEG_my",
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun deletePicture() {
        if (currentPhotoPath == ""){
            return
        }
        val f = File(currentPhotoPath)
        f.delete()
        currentPhotoPath = ""
        val text = R.string.delete_photo
        val duration = Toast.LENGTH_SHORT
        Toast.makeText(context, text, duration).show()
    }

    private fun post():String {
        val uploader = activity?.let { UploadUtility(it) }
        uploader?.uploadFile(currentPhotoPath)
        return ("Все ок")
    }

    //doesn't work, I am fucking it!
    private fun galleryAddPic() {
        val cont = activity?.applicationContext

        if (cont != null) {
            cont.fileList()
            val f = File(currentPhotoPath)
            try {
                activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                MediaScannerConnection.scanFile(cont, arrayOf(f.toString()),
                        null, null)
                //MediaScannerConnection.scanFile(Cont, arrayOf(f.toString()), null, null)

            }
            catch (e: IOException) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                Log.w("ExternalStorage", "Error writing $f", e)
            }
        }
    }
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }
}


