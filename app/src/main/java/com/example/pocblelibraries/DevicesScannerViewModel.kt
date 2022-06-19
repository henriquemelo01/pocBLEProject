package com.example.pocblelibraries

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import java.util.*
import java.util.concurrent.TimeUnit

class DevicesScannerViewModel : ViewModel() {

    val velocityDataSet = mutableListOf<Entry>()

    var wasFirstNotification = true

    var startTime = 0L

    fun addVelocityData(value: Float) {

        if(wasFirstNotification) {
            startTime = Calendar.getInstance().time.time
        }

        val currentTime = Calendar.getInstance().time.time

        val timeDiffInMillis = TimeUnit.MILLISECONDS.toMillis(currentTime - startTime)
        val timeDiffInSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime - startTime)
//        println("DiffTime in millis: $timeDiffInMillis / DiffTime in seconds: $timeDiffInSeconds")

        velocityDataSet.add(Entry(timeDiffInSeconds.toFloat(), value))
    }
}