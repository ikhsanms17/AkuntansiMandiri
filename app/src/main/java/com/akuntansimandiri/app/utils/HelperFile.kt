package com.akuntansimandiri.app.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveReceivedFileToDownload(
    context: Context,
    fileChunks: ByteArray,
    childFolder: String,
    fileName: String,
    onSuccess: (() -> Unit)? = null,
    onFail: (() -> Unit)? = null,
) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ (uses MediaStore)
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/$childFolder")
            }

            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            )

            if (uri != null) {
                try {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(fileChunks)
                        Logger.d("Helper", "File received and saved at: $uri")
                        onSuccess?.invoke()
                    }
                } catch (e: IOException) {
                    Logger.e("Helper", "Error writing the received file: ${e.message}")
                    onFail?.invoke()
                }
            } else {
                Logger.e("Helper", "Failed to insert the file into MediaStore.")
                onFail?.invoke()
            }
        } else {
            // Pre-Android 10 (uses File API)
            val downloadDirectory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                childFolder
            )

            if (!downloadDirectory.exists()) {
                downloadDirectory.mkdirs()
            }

            val receivedFile = File(downloadDirectory, fileName)

            try {
                FileOutputStream(receivedFile).use { fos ->
                    fos.write(fileChunks)
                    Log.d("Helper", "File received and saved at: ${receivedFile.absolutePath}")
                    onSuccess?.invoke()
                }
            } catch (e: IOException) {
                Logger.e("Helper", "Error writing the received file: ${e.message}")
                onFail?.invoke()
            }
        }
    } catch (e: Exception) {
        Logger.e("Helper", "Unexpected error: ${e.message}")
        onFail?.invoke()
    }
}
