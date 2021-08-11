package com.example.cameratest.ui.main

import android.content.Intent
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
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
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String
    lateinit var PhotoPath: Uri


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        button_show_photo.setOnClickListener {
            imageView.setImageURI(PhotoPath)
        }
        button_delete.setOnClickListener {
            deletePicture()
        }
        button_open_camera.setOnClickListener {
            dispatchTakePictureIntent()
            galleryAddPic()
        }
        button_post.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main){
                val NameStr = withContext(Dispatchers.Default) {
                    post()
                }
                val duration = Toast.LENGTH_SHORT
                Toast.makeText(context, NameStr, duration).show()
            }

        }
        //dispatchTakePictureIntent()
        //galleryAddPic()

    }

    private fun dispatchTakePictureIntent() {
        val activity = getActivity()
        if(activity != null) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity.getPackageManager())?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created

                val Cont = activity?.applicationContext
                if (Cont != null) {
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                Cont,
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
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
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

    suspend fun post():String {
        val client = OkHttpClient()
        val url = URL("https://reqres.in/api/users")

        //just a string
        var jsonString = "{\"name\": \"Rolando\", \"job\": \"Fakeador\"}"

        //or using jackson
        val mapperAll = ObjectMapper()
        val jacksonObj = mapperAll.createObjectNode()
        jacksonObj.put("name", "Rolando")
        jacksonObj.put("job", "Fakeador")
        val jacksonString = jacksonObj.toString()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jacksonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(request).execute()

        val responseBody = response.body!!.string()

        //Response
        println("Response Body: " + responseBody)

        //we could use jackson if we got a JSON
        val objData = mapperAll.readTree(responseBody)

        println("My name is " + objData.get("name").textValue() + ", and I'm a " + objData.get("job").textValue() + ".")
        return objData.get("name").textValue()
    }
    //doesn't work, I am fucking it!
    private fun galleryAddPic() {
        val Cont = activity?.applicationContext

        if (Cont != null) {
            var files: Array<String> = Cont.fileList()
            val f = File(currentPhotoPath)

            try {
                //MediaScannerConnection.scanFile(Cont, arrayOf(f.toString()), null, null)
                MediaScannerConnection.scanFile(Cont, arrayOf(f.toString()), null,
                        OnScanCompletedListener { path, uri ->
                            Log.i("ExternalStorage", "Scanned $path:")
                            Log.i("ExternalStorage", "-> uri=$uri")
                        })
            }
            catch (e: IOException) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                Log.w("ExternalStorage", "Error writing " + f, e)
            }

        }
    }
}


