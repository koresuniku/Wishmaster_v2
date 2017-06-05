package com.koresuniku.wishmaster.presenter

import android.os.Environment
import android.util.Log
import com.koresuniku.wishmaster.R
import com.koresuniku.wishmaster.presenter.view_interface.SaveFileView
import com.koresuniku.wishmaster.util.StringUtils
import org.jetbrains.anko.custom.async
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class FileSaver(val view: SaveFileView) {
    val LOG_TAG = "FileSaver"

    fun saveFileToExternalStorage(urlString: String, fileName: String) {

        if (!PermissionManager.checkWriteExternalStoragePermission(view.getActivity())) {
            PermissionManager.requestWriteExternalStoragePermission(view.getActivity())
            Log.d(LOG_TAG, "need to request permission")
            return
        }

        async {


            try {
                val dir: File = File(Environment.getExternalStorageDirectory().absolutePath
                        + "/" + view.getContext().getString(R.string.download_dir)
                        + "/" + view.getContext().getString(R.string.app_name))
                if (!dir.exists()) dir.mkdirs()
                Log.d(LOG_TAG, "dir: " + dir.absolutePath)
                var file: File = File(dir, fileName)
                var counter = 0

                while (file.exists()) {
                    file = File(dir, StringUtils.getFilenameString(fileName, ++counter))
                }


                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connect()
                val fileOutput = FileOutputStream(file)
                val inputStream = connection.getInputStream()
                val totalSize = connection.contentLength
                var downloadedSize = 0
                val buffer = ByteArray(1024)
                var bufferLength = inputStream.read(buffer)
                while (bufferLength > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    //updateProgress(downloadedSize, totalSize);
                    bufferLength = inputStream.read(buffer)
                }
                Log.d(LOG_TAG, "file created")


            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(LOG_TAG, "exception")
            }
        }
    }
}