package com.scott.phonedetective.data

import android.os.Build

class DeviceManager {
    fun getDeviceModel(): String {
        val manufacturer = Build.MANUFACTURER ?: "Unknown"
        val model = Build.MODEL ?: "Unknown"
        return if (model.lowercase().startsWith(manufacturer.lowercase())) {
            model
        } else {
            "$manufacturer $model"
        }
    }

    fun getOsVersion(): String {
        val release = Build.VERSION.RELEASE ?: "Unknown"
        val sdkInt = Build.VERSION.SDK_INT
        return "Android $release (API $sdkInt)"
    }
}
