package com.kardabel.realestatemanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.junit.Assert.fail
import java.util.concurrent.atomic.AtomicBoolean

fun <T> LiveData<T>.observeForTesting(block: (T) -> Unit) {
    val triggered = AtomicBoolean(false)

    val observer = Observer<T> {
      //if (!triggered.compareAndSet(false, true)) {
      //    fail("LiveData triggered more than once !")
      //}
    }
    try {
        observeForever(observer)
        value?.let(block) ?: fail("LiveData value was null !")

     // if (!triggered.get()) {
     //     fail("LiveData was never triggered !")
     // }
    } finally {
        removeObserver(observer)
    }
}