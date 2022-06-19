package com.example.pocblelibraries.utils

import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.pocblelibraries.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet

fun Context.hasPermission(permissionType: String) =
    ContextCompat.checkSelfPermission(this, permissionType) == PackageManager.PERMISSION_GRANTED

fun BluetoothGattCharacteristic.isReadable() : Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

fun BluetoothGattCharacteristic.isNotifyCharacteristic() : Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean = properties and property != 0

fun ByteArray.toHexString() : String =
    joinToString(separator = "", prefix = "0x") { String.format("%02X", it) }

fun ByteArray.processIntegerData() : Int = toHexString().takeLast(6).filterIndexed { index, _ ->
    index % 2 != 0}.toInt()

fun ByteArray.processFloatData() : Float {
    val hexStringData = this.toHexString().takeLast(8)
    return "${hexStringData[1]}.${hexStringData[5]}${hexStringData[7]}".toFloat()
}

fun LineChart.setupStyle() = this.apply {

    axisRight.isEnabled = false

    axisLeft.apply {
        isEnabled = true
        axisMinimum = 0f
        axisMaximum = 1.2f
    }

    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
    }

    setTouchEnabled(true)

    isDragEnabled = true

    description = null

    enableScroll()
}

fun LineDataSet.setupStyle(context: Context) = this.apply {
    setDrawValues(false)
    lineWidth = 3f
    mode = LineDataSet.Mode.CUBIC_BEZIER
    isHighlightEnabled = true
    setDrawHighlightIndicators(false)
    setDrawCircles(false)
    color = ContextCompat.getColor(context, R.color.green)
}