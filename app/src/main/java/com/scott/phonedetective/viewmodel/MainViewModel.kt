package com.scott.phonedetective.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.scott.phonedetective.data.BatteryMonitor
import com.scott.phonedetective.data.DeviceManager
import com.scott.phonedetective.model.DeviceInfo

class MainViewModel : ViewModel() {
    private val _deviceInfo = MutableLiveData<DeviceInfo>()
    val deviceInfo: LiveData<DeviceInfo> = _deviceInfo

    fun loadDeviceInfo(deviceManager: DeviceManager, batteryMonitor: BatteryMonitor) {
        _deviceInfo.value = DeviceInfo(
            model = deviceManager.getDeviceModel(),
            osVersion = deviceManager.getOsVersion(),
            batteryLevel = batteryMonitor.getBatteryLevel()
        )
    }
}
