package com.anand.modelprojectforapi.currentlocation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

class CurrentLocationClass(val context: Context,val locationCallBack :LocationCallBack) {
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private fun isPermissionGranted()= ContextCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun enableMyLocation(activity: Activity) {
        if (isPermissionGranted()) {
            Log.d("CurrentLocation","isMyLocationEnabled is true")
            getCurrentLocation(activity)
        } else { ActivityCompat.requestPermissions(activity,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,activity:Activity) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation(activity)
            }
        }
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                addressText = address.locality
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }
        return addressText
    }

    fun getCurrentLocation(activity: Activity){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation.addOnSuccessListener{ location: Location? ->
            location?.let {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val address = getAddress(currentLatLng)

                Log.d("CurrentLocation",address)
                locationCallBack.callbackFromLocation(address)
            }
        }
    }

}

interface LocationCallBack{
    fun callbackFromLocation(address: String)
}
