package com.example.pocblelibraries.bluetooth

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class BluetoothFragment: Fragment() {

    private val permissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    protected fun checkPermissions() {
        if (allPermissionsGranted()) {
            onPermissionGranted()
        } else {
            requestPermissions(permissions, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS)
            if (allPermissionsGranted())
                onPermissionGranted()
            else
                onPermissionDenied()
    }

    private fun onPermissionGranted() {
        gpsNeeded()
    }

    private fun onPermissionDenied() {
        Toast.makeText(
            requireContext(),
            "Permissions needed to scan bluetooth devices",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun gpsNeeded(){
        if(checkGPSIsOpen())
            onPermissionsAccepted()
        else{
            val alertDialog: AlertDialog? = activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setTitle("Habilite o GPS")
                    setMessage("Por favor ative o gps")
                    setPositiveButton("Ativar GPS") {dialog,_ ->
                        actvateGps()
                        dialog.dismiss()
                    }
                    setNegativeButton("Agora não") {dialog,_ ->
                        dialog.dismiss()
                    }
                }
                builder.create()
            }
            alertDialog?.show()
        }
    }

    private fun checkGPSIsOpen(): Boolean {
        val locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun actvateGps(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    abstract fun onPermissionsAccepted()

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 9999
    }
}