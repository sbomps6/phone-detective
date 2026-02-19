package com.scott.phonedetective.data

import org.junit.Test
import kotlin.test.assertTrue

class DeviceManagerTest {
    @Test
    fun `getDeviceModel should return a non-empty string`() {
        val deviceManager = DeviceManager()
        val model = deviceManager.getDeviceModel()
        assertTrue(model.isNotEmpty(), "Device model should not be empty")
    }

    @Test
    fun `getOsVersion should return a non-empty string`() {
        val deviceManager = DeviceManager()
        val version = deviceManager.getOsVersion()
        assertTrue(version.isNotEmpty(), "OS version should not be empty")
    }
}
