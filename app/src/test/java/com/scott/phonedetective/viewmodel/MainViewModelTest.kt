package com.scott.phonedetective.viewmodel

import org.junit.Test
import kotlin.test.assertNotNull

class MainViewModelTest {
    @Test
    fun `viewModel should be initializable`() {
        // This will fail compilation initially
        val viewModel = MainViewModel()
        assertNotNull(viewModel)
    }
}
