package com.example.pocblelibraries.bluetooth

import android.view.View
import com.example.pocblelibraries.R
import com.example.pocblelibraries.databinding.ItemFoundedDeviceBinding
import com.example.pocblelibraries.model.BleDeviceModel
import com.xwray.groupie.viewbinding.BindableItem

class FoundedDeviceItem(
    private val device: BleDeviceModel,
    private val onConnectClick: (() -> Unit)? = null
) : BindableItem<ItemFoundedDeviceBinding>() {

    private val bleDeviceState: BleDeviceState = BleDeviceState.DISCONNECT

    override fun getLayout() = R.layout.item_founded_device

    override fun bind(viewBinding: ItemFoundedDeviceBinding, position: Int): Unit = with(viewBinding) {
        tvDeviceName.text = device.deviceName
        btConnectToDevice.apply {
            text =
                if(bleDeviceState == BleDeviceState.CONNECTED) BleDeviceState.DISCONNECT.label
                else BleDeviceState.CONNECTED.label

            setOnClickListener {
                onConnectClick?.invoke()
            }

        }
    }

    override fun initializeViewBinding(view: View) = ItemFoundedDeviceBinding.bind(view)

    private enum class BleDeviceState(val label: String) {
        CONNECTED("Conectar"),
        DISCONNECT("Desconectar");
    }
}