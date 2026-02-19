package com.scott.phonedetective

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.scott.phonedetective.data.BatteryMonitor
import com.scott.phonedetective.data.DeviceManager
import com.scott.phonedetective.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvDeviceModel = findViewById<TextView>(R.id.tvDeviceModel)
        val tvOsVersion = findViewById<TextView>(R.id.tvOsVersion)
        val tvBatteryLevel = findViewById<TextView>(R.id.tvBatteryLevel)

        viewModel.deviceInfo.observe(this) { info ->
            tvDeviceModel.text = info.model
            tvOsVersion.text = info.osVersion
            tvBatteryLevel.text = "${info.batteryLevel}%"
        }

        // Load device info
        val deviceManager = DeviceManager()
        val batteryMonitor = BatteryMonitor(this)
        viewModel.loadDeviceInfo(deviceManager, batteryMonitor)
    }
}
