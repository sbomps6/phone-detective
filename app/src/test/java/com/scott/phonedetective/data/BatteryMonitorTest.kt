package com.scott.phonedetective.data

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class BatteryMonitorTest {
    @Test
    fun `calculateLevel should return correct percentage from intent`() {
        // We'll make this method internal/protected or use a public helper for testing
        val context = mockk<Context>()
        val intent = mockk<Intent>()
        
        every { intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) } returns 42
        every { intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) } returns 100
        
        val batteryMonitor = BatteryMonitor(context)
        val level = batteryMonitor.calculateLevel(intent)
        
        assertEquals(42, level)
    }
}
