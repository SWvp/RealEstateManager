package com.kardabel.realestatemanager.utils

import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConvertMoneyTest {

    @Test
    fun checkConvertDollarToEuro() {
        val value: Int = 25
        val expected: Int = 20
        assertEquals(expected, Utils.convertDollarToEuro(value))
    }
    @Test
    fun convertEuroToDollar() {
        val value: Int = 20
        val expected: Int = 25
        Assert.assertEquals(expected, Utils.convertEuroToDollar(value))
    }

}