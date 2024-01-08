package com.example.weatherapp.model

object Info {
    var userInfo: UserInformation = UserInformation()
    lateinit var viewModel: MainViewModel
}

data class UserInformation(
    var state: String = "",
    var city: String = "",
    var currentLatitude:Double? =null,
    var currentLongitude:Double? = null,
    var useCurrent:Boolean = false
)