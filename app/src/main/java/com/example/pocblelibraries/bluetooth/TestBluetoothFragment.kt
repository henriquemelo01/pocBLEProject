package com.example.pocblelibraries.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.welie.blessed.*

abstract class TestBluetoothFragment : Fragment() {

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

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
                }

                override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {

                }

                override fun onDisconnectedPeripheral(
                    peripheral: BluetoothPeripheral,
                    status: HciStatus
                ) {}

                override fun onConnectionFailed(
                    peripheral: BluetoothPeripheral,
                    status: HciStatus
                ) {}
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


    private fun turnOnBluetooth() {

        central.isBluetoothEnabled

        if(!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            onBluetoothStateChangeActivityResult.launch(enableBtIntent)
        }
    }
}