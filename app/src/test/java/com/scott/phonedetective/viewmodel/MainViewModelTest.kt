package com.scott.phonedetective.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.scott.phonedetective.data.BatteryMonitor
import com.scott.phonedetective.data.DeviceManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MainViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `viewModel should be initializable`() {
        val viewModel = MainViewModel()
        assertNotNull(viewModel)
    }

    @Test
    fun `loadDeviceInfo should update LiveData`() {
        val viewModel = MainViewModel()
        val deviceManager = mockk<DeviceManager>()
        val batteryMonitor = mockk<BatteryMonitor>()

        every { deviceManager.getDeviceModel() } returns "Pixel 7"
        every { deviceManager.getOsVersion() } returns "Android 14"
        every { batteryMonitor.getBatteryLevel() } returns 85

        viewModel.loadDeviceInfo(deviceManager, batteryMonitor)

        val info = viewModel.deviceInfo.value
        assertNotNull(info)
        assertEquals("Pixel 7", info.model)
        assertEquals("Android 14", info.osVersion)
        assertEquals(85, info.batteryLevel)
    }
}
