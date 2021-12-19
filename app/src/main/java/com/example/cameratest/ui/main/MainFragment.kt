package com.example.cameratest.ui.main

import URIPathHelper
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhotoPathL: String = ""
    private var currentPhotoPathR: String = ""
    lateinit var PhotoPathL: Uri
    lateinit var PhotoPathR: Uri
    private val KEY_INDEX = "index"
    val REQUEST_CODE = 100
    var checkForL = true


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putBoolean(IS_EDITING_KEY, isEditing)
        //outState.putString(KEY_INDEX, textView1.text as String?)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val s = savedInstanceState?.getString(KEY_INDEX, "1") ?: "1"
        foot_left.setOnClickListener {
            dispatchTakePictureIntent(1)
            galleryAddPic()
            foot_left.setImageURI(this.PhotoPathL)
        }
        foot_right.setOnClickListener {
            dispatchTakePictureIntent(2)
            galleryAddPic()
            foot_right.setImageURI(this.PhotoPathR)
        }
        button_help.setOnClickListener {
            setAlert(R.string.help, "Инструкция")
        }
        /*
        button_show_photo.setOnClickListener {
            if (currentPhotoPathL !== ""){
                imageView.setImageURI(this.PhotoPathL)
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
         */
        button_find_left.setOnClickListener {
            this.checkForL = true
            openGalleryForImage()
        }
        button_find_right.setOnClickListener {
            this.checkForL = false
            openGalleryForImage()
        }
        button_post.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val nameStr = withContext(Dispatchers.Default) {
                    post()
                }
                val duration = Toast.LENGTH_SHORT
                Toast.makeText(context, nameStr, duration).show()
            }

        }
        //dispatchTakePictureIntent()
        //galleryAddPic()

    }

    fun setAlert(str: Int, nameTitle: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(nameTitle)
        builder.setMessage(str)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(
                context,
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()
        }

        builder.show()
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            val uriPathHelper = URIPathHelper()
            if (checkForL) {
                foot_left.setImageURI(data?.data) // handle chosen image
                PhotoPathL = data?.data!!
                val filePath = uriPathHelper.getPath(context!!, PhotoPathL)
                if (filePath != null) {
                    currentPhotoPathL = filePath
                }

            } else {
                foot_right.setImageURI(data?.data) // handle chosen image
                PhotoPathR = data?.data!!
                val filePath = uriPathHelper.getPath(context!!, PhotoPathR)
                if (filePath != null) {
                    currentPhotoPathR = filePath
                }
            }

        }
        if (currentPhotoPathL !== "") {
            foot_left.setImageURI(this.PhotoPathL)
        }
        if (currentPhotoPathR !== "") {
            foot_right.setImageURI(this.PhotoPathR)
        }
    }


    private fun dispatchTakePictureIntent(PhotoPathReal: Int) {
        val activity = activity
        if (activity != null) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(activity.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile(PhotoPathReal)
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

                            if (PhotoPathReal == 1) {
                                this.PhotoPathL = photoURI
                            } else {
                                this.PhotoPathR = photoURI
                            }
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
    private fun createImageFile(PhotoPathReal: Int): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        //val storageDir1: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            //"JPEG_my",
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (PhotoPathReal == 1) {
                currentPhotoPathL = absolutePath
            } else {
                currentPhotoPathR = absolutePath
            }
        }
    }

    private fun deletePicture() {
        if (currentPhotoPathL == "") {
            return
        }
        val f = File(currentPhotoPathL)
        f.delete()
        currentPhotoPathL = ""
        val text = R.string.delete_photo
        val duration = Toast.LENGTH_SHORT
        Toast.makeText(context, text, duration).show()
    }

    private fun post(): String {
        val uploader = activity?.let { UploadUtility(it) }
        uploader?.uploadFile(currentPhotoPathL, currentPhotoPathR)
        return ("Все ок")
    }

    //doesn't work, I am fucking it!
    private fun galleryAddPic() {
        val cont = activity?.applicationContext

        if (cont != null) {
            cont.fileList()
            val f = File(currentPhotoPathL)
            try {
                activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                MediaScannerConnection.scanFile(
                    cont, arrayOf(f.toString()),
                    null, null
                )
                //MediaScannerConnection.scanFile(Cont, arrayOf(f.toString()), null, null)

            } catch (e: IOException) {
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


