package com.example.pocblelibraries

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ConnectionState
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DevicesScannerViewModel : ViewModel() {

    val foundedPeripherals = mutableListOf<BluetoothPeripheral>()

    private val peripheralsStateList get() = foundedPeripherals.associate { peripheral ->
        peripheral.name to MutableLiveData(peripheral.state.toBluetoothPeriapheralState())
    }

    val velocityDataSet = mutableListOf<Entry>()

    fun getPeripheralStateLiveDataByName(name: String) = peripheralsStateList[name]


    var wasFirstNotification = true
    var startTime = 0L

    fun addVelocityData(value: Float) {

        if(wasFirstNotification) {
            startTime = Calendar.getInstance().time.time
        }

        val currentTime = Calendar.getInstance().time.time
        val timeDiffInMillis = TimeUnit.MILLISECONDS.toMillis(currentTime - startTime)
        val timeDiffInSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime - startTime)
//        println("DiffTime in millis: $timeDiffInMillis / DiffTime in seconds: $timeDiffInSecons")

        velocityDataSet.add(Entry(timeDiffInSeconds.toFloat(), value))
    }


    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
}

enum class BluetoothPeripheralState(val value: String) {
    CONNECTED("Conectado"),
    DISCONNECTED("Desconectado"),
    CONNECTING("Conectando"),
    DISCONNECTING("Desconectando");
}

fun ConnectionState.toBluetoothPeriapheralState() = when(this) {
    ConnectionState.CONNECTED -> BluetoothPeripheralState.CONNECTED
    ConnectionState.CONNECTING -> BluetoothPeripheralState.CONNECTING
    ConnectionState.DISCONNECTING -> BluetoothPeripheralState.DISCONNECTING
    else -> BluetoothPeripheralState.DISCONNECTED
}