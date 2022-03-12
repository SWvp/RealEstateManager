package com.kardabel.realestatemanager.utils

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConvertMoneyTest {

    @Test
    fun checkConvertDollarToEuro() {
        assertEquals(81, Utils.convertDollarToEuro(100))
    }
}