package com.koresuniku.wishmaster.presenter

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.koresuniku.wishmaster.util.DeviceUtils

object PermissionManager {
    val LOG_TAG = PermissionManager::class.java.simpleName!!

    val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 0


    fun checkWriteExternalStoragePermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun requestWriteExternalStoragePermission(activity: Activity) {
        if (DeviceUtils.sdkIsMarshmallowOrHigher()) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_STORAGE_PERMISSION_CODE)
        }
    }

}
