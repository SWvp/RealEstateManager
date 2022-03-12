package com.kardabel.realestatemanager.utils

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@RunWith(JUnit4::class)
class DateTest {

    @Test
    fun `todayDate give correct format`() {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dateFormat.parse(Utils.todayDate())
        assertEquals(dateFormat.format(date!!), Utils.todayDate())
    }
}