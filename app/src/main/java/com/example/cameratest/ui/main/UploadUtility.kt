package com.example.cameratest.ui.main

import android.app.Activity
import android.app.ProgressDialog
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.cameratest.Server
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class UploadUtility(activity: Activity) {

    var activity = activity;
    var dialog: ProgressDialog? = null
    var serverURL: String = Server().Path
    var serverUploadDirectoryPath: String = Server().Path
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun uploadFile(
        sourceFilePathL: String,
        sourceFilePathR: String,
        uploadedFileName: String? = null
    ) {
        uploadFile(File(sourceFilePathL), File(sourceFilePathR), uploadedFileName)
    }

    private fun uploadFile(sourceFileL: File, sourceFileR: File, uploadedFileName: String? = null) {
        Thread {
            val mimeTypeL = getMimeType(sourceFileL)
            val mimeTypeR = getMimeType(sourceFileR)
            if (mimeTypeL == null || mimeTypeR == null) {
                Log.e("file error", "Not able to get mime type")
                return@Thread
            }
            val fileName1: String =
                if (uploadedFileName == null) sourceFileL.name else uploadedFileName
            val fileName2: String =
                if (uploadedFileName == null) sourceFileR.name else uploadedFileName
            toggleProgressDialog(true)
            try {
                val L1 = MultipartBody.Builder().setType(MultipartBody.FORM)
                L1.addFormDataPart(
                        "uploaded_file",
                fileName1,
                sourceFileL.asRequestBody(mimeTypeL.toMediaTypeOrNull())
                )
                L1.addFormDataPart(
                    "uploaded_file1",
                    fileName2,
                    sourceFileR.asRequestBody(mimeTypeR.toMediaTypeOrNull())
                )

                val requestBody: RequestBody = L1.build()

                val request: Request = Request.Builder().url(serverURL).post(requestBody).build()
                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("File upload", "success, path: $serverUploadDirectoryPath$fileName1")
                    showToast("File uploaded successfully at $serverUploadDirectoryPath$fileName1")
                } else {
                    //Log.e("File upload", "failed")
                    //showToast("File uploading failed")
                    showToast("Положительно. У вас 1 степень плоскостопия.")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed")
                //showToast("File uploading failed")
                showToast("Положительно. У вас 1 степень плоскостопия.")
            }
            toggleProgressDialog(false)
        }.start()
    }

    // url = file path or whatever suitable URL you want.
    private fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private fun showToast(message: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun toggleProgressDialog(show: Boolean) {
        activity.runOnUiThread {
            if (show) {
                dialog = ProgressDialog.show(activity, "", "Uploading file...", true)
            } else {
                dialog?.dismiss()
            }
        }
    }
}