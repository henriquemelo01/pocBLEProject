package com.example.pocblelibraries.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.pocblelibraries.databinding.FragmentDevicesScannerBinding
import com.example.pocblelibraries.model.BleDeviceModel
import com.example.pocblelibraries.utils.processIntegerData
import com.example.pocblelibraries.utils.processFloatData
import com.example.pocblelibraries.utils.setupStyle
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.welie.blessed.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.util.*

class DevicesScannerFragment : BluetoothFragment() {

    private lateinit var binding: FragmentDevicesScannerBinding

    private val foundedDevices = mutableListOf<String>()

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val viewModel: DevicesScannerViewModel by viewModels()

    private var deviceConnected: BluetoothPeripheral? = null

    private val central by lazy {
        BluetoothCentralManager(
            requireContext(),
            object : BluetoothCentralManagerCallback() {

                override fun onScanFailed(scanFailure: ScanFailure) {
                    println("Falhou: ${scanFailure.value}")
                }

                override fun onDiscoveredPeripheral(
                    peripheral: BluetoothPeripheral,
                    scanResult: ScanResult
                ) {
                    if (!foundedDevices.contains(peripheral.name) && peripheral.name.isNotEmpty()) {
                        foundedDevices.add(peripheral.name)
                        adapter.add(
                            FoundedDeviceItem(BleDeviceModel(deviceName = peripheral.name, macAddress = peripheral.address)) {
                                onConnectionClick(peripheral)
                            }
                        )
                    }
                }

                override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {
                    println("onConnectedPeripheral - connected to ${peripheral.name}")

                    deviceConnected = peripheral

                    binding.tvConnectedPeripheral.text = "Dispositivo conectado: ${peripheral.name}"

                    binding.btRemoveConnection.visibility = View.VISIBLE

                    foundedDevices.clear()
                    adapter.clear()
                }

                override fun onDisconnectedPeripheral(
                    peripheral: BluetoothPeripheral,
                    status: HciStatus
                ) {
                    println("onDisconnectedPeripheral - disconnected to ${peripheral.name}")
                }

                override fun onConnectionFailed(
                    peripheral: BluetoothPeripheral,
                    status: HciStatus
                ) {
                    println("onConnectionFailed - failure to connect to ${peripheral.name}\nFailure: $status")
                }
            },
            Handler(Looper.getMainLooper())
        )
    }

    private val onBluetoothStateChangeActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Toast
                    .makeText(requireContext(), "Bluetooth permission was accepted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                turnOnBluetooth()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDevicesScannerBinding.inflate(inflater, container, false).apply{
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btScanBleDevices.setOnClickListener{
            checkPermissions()
            viewModel.velocityDataSet.clear()
        }

        binding.rvFoundedBleDevicesList.adapter = adapter

        binding.btRemoveConnection.setOnClickListener{
            deviceConnected?.let{
                println(viewModel.velocityDataSet)
                central.cancelConnection(it)
                binding.btRemoveConnection.visibility = View.GONE
            }
        }

        binding.ctDevicesScannerFragment.setLeftIconClickListener {
            Toast.makeText(requireContext(), "Left Icon Click", Toast.LENGTH_SHORT).show()
        }

        binding.ctDevicesScannerFragment.setRightIconClickListener {
            Toast.makeText(requireContext(), "Right Icon Click", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        turnOnBluetooth()
    }

    override fun onPermissionsAccepted() {
        if(central.isScanning)
            central.stopScan()
         else
             central.scanForPeripherals()
    }

    private fun onConnectionClick(peripheral: BluetoothPeripheral) {
        central.stopScan()
        Toast.makeText(requireContext(), "Conectando ao ${peripheral.name}", Toast.LENGTH_SHORT).show()
        central.connectPeripheral(peripheral, BleConnectionHandler(binding, viewModel, requireContext()))
    }

    private class BleConnectionHandler(
        private val binding: FragmentDevicesScannerBinding,
        private val viewModel: DevicesScannerViewModel,
        private val context: Context
    ) : BluetoothPeripheralCallback() {

        override fun onServicesDiscovered(peripheral: BluetoothPeripheral) {
            with(peripheral) {
                // integer (xxx) -> dummyNumberOfRepetitions
                getCharacteristic(BLE_SERVICE_UUID, BLE_CHARACTERISTIC_TX_UUID)?.let {
                    setNotify(it, true)
                }

                // float (x.xx) -> dummyVelocity ou dummyAcceleration
                getCharacteristic(BLE_SERVICE_UUID, BLE_VELOCITY_CHARACTERISTIC_UUID)?.let {
                    setNotify(it, true)
                }
            }
        }

        override fun onNotificationStateUpdate(
            peripheral: BluetoothPeripheral,
            characteristic: BluetoothGattCharacteristic,
            status: GattStatus
        ) {
            if(status == GattStatus.SUCCESS) {
                // onSuccess of setNotification
                if(peripheral.isNotifying(characteristic)) {
                    binding.tvPeripheralData.visibility = View.VISIBLE

                    if(characteristic.uuid == BLE_VELOCITY_CHARACTERISTIC_UUID)
                        viewModel.wasFirstNotification = true

                }
                else {
                    binding.tvPeripheralData.visibility = View.GONE
                }
            } else {
                // onFailure of setNotification
                Log.i("BleConnectionHandler", String.format("ERROR: Changing notification state failed for %s", characteristic.uuid));
            }
        }

        override fun onCharacteristicUpdate(
            peripheral: BluetoothPeripheral,
            value: ByteArray,
            characteristic: BluetoothGattCharacteristic,
            status: GattStatus
        ) {

            if(characteristic.uuid == BLE_CHARACTERISTIC_TX_UUID)
                binding.tvPeripheralData.text = "Valor lido: ${value.processIntegerData()}"

            else if(characteristic.uuid == BLE_VELOCITY_CHARACTERISTIC_UUID) {
                binding.tvVelocityCharacteristicData.text = "Velocidade: ${value.processFloatData()}"

                viewModel.addVelocityData(value.processFloatData())

                binding.lcVelocityData.apply {
                    setupStyle()

                    visibility = if(viewModel.velocityDataSet.isNotEmpty()) View.VISIBLE else View.GONE

                    val velocityEntries = viewModel.velocityDataSet

                    val dataSet = LineDataSet(velocityEntries, VELOCITY_CHART_LABEL).setupStyle(context)

                    data = LineData(dataSet)

                    invalidate()
                }
                viewModel.wasFirstNotification = false
            }
        }
    }

    private fun turnOnBluetooth() {
        if(!central.isBluetoothEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            onBluetoothStateChangeActivityResult.launch(enableBtIntent)
        }
    }

    private companion object {
        val BLE_SERVICE_UUID: UUID = UUID.fromString("f8ab3678-b2b6-11ec-b909-0242ac120002")
        val BLE_CHARACTERISTIC_TX_UUID: UUID = UUID.fromString("f8ab3a6a-b2b6-11ec-b909-0242ac120002")
        val BLE_VELOCITY_CHARACTERISTIC_UUID: UUID = UUID.fromString("9a3843fe-ed19-11ec-8ea0-0242ac120002")

        const val VELOCITY_CHART_LABEL = "Velocity"
    }
}