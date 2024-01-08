package com.example.weatherapp.model

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val locationClient = LocationServices.getFusedLocationProviderClient(application)
    private val _location = MutableLiveData<String>()
    private val _isLoading = MutableLiveData(false)
    val _latitude = MutableLiveData<Double>()
    val _longitude = MutableLiveData<Double>()

    fun fetchLocation() {
        _isLoading.value = true
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    _latitude.value = location.latitude
                    _longitude.value = location.longitude
                    _location.value = "Lat: ${location.latitude}, Long: ${location.longitude}"
                }
            }
        }

        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        _isLoading.value = true
        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                _latitude.value = it.latitude
                _longitude.value = it.longitude
                _location.value = "Lat: ${it.latitude}, Long: ${it.longitude}"
            } ?: run {
                requestLocationUpdates()
            }
            _isLoading.value = false
        }.addOnFailureListener {
            _location.value = "Error fetching location: ${it.message}"
            _isLoading.value = false
            requestLocationUpdates()
        }
    }
}
