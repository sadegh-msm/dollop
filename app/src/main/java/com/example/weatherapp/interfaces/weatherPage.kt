package com.example.weatherapp.interfaces

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weatherapp.model.Forecast
import com.example.weatherapp.api.getAQIHandler
import com.example.weatherapp.api.getCoordinatesByCity
import com.example.weatherapp.api.weatherHandler
import com.example.weatherapp.model.Info
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource
import com.example.weatherapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            )
            {
                val chosenText:String
                if(!Info.userInfo.useCurrent){
                    chosenText = Info.userInfo.city + " Weather"
                }
                else{
                    chosenText = " Weather"
                }
                Text(
                    text = chosenText,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    )
}

fun getColor(aqi:Int):Pair<Color,String>{
    if(aqi <=50){
        return Pair(Color.Green,"Good")
    }
    if(aqi <=100){
        return Pair(Color.Yellow,"Moderate")
    }
    if(aqi <=150){
        return Pair(Color(243,106,12),"Unhealthy for Sensitive Groups")
    }
    if(aqi <200){
        return Pair(Color.Red,"Unhealthy")
    }
    if(aqi <=300){
        return Pair(Color(220,0,209),"Very Unhealthy")
    }
    else{
        return Pair(Color(137,0,0),"Hazardous")
    }
}

@Composable
fun ShowWeatherPage(navHostController: NavHostController){
    showList(navController = navHostController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showList(navController: NavHostController) {
    val weatherListState = remember { mutableStateListOf<Forecast>() }
    var aqiState = remember { mutableStateOf(0) }
    var isLoad by remember { mutableStateOf(false) }
    var showErrorDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (!isLoad) {
        LaunchedEffect(key1 = Unit) {
            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            scope.launch {
                val (ok1, weathers) = getWeatherByCity(Info.userInfo.city, Info.userInfo.currentLatitude, Info.userInfo.currentLongitude)
                val (ok2, aqi) = getAQI(Info.userInfo.city, Info.userInfo.currentLatitude, Info.userInfo.currentLongitude)

                if (weathers != null && aqi != null && ok1 == "ok" && ok2 == "ok") {
                    weatherListState.addAll(weathers)
                    aqiState.value = aqi
                    isLoad = true
                } else {
                    showErrorDialog.value = true
                }
            }
        }
    }

    if (showErrorDialog.value) {
        ErrorDialog(
            onDismiss = { navController.navigate("home_page") },
            text = "Connection Error"
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar()
                if (isLoad) {
                    val (color, level) = getColor(aqiState.value)
                    AQIBar(aqiState.value, level, color)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(all = 10.dp), contentPadding = padding) {
            items(weatherListState) { weatherInfo ->
                WeatherCard(
                    weatherInfo = weatherInfo,
                    aqi = aqiState.value,
                    modifier = Modifier.padding(1.dp),
                    navController = navController
                )
            }
        }
    }
}


@Composable
fun AQIBar(aqi: Int, level: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "AQI: $aqi",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.background
                )
                Text(
                    text = "Level: $level",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.background
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = aqi.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun WeatherCard(weatherInfo: Forecast, aqi: Int, modifier: Modifier, navController: NavHostController) {
    Card(
        modifier = modifier
            .padding(all = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(size = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (weatherInfo.weather.first().main == "Clear"){
                    Image(
                        painter = painterResource(id = R.drawable.clear),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                    )
                } else if (weatherInfo.weather.first().main == "Clouds") {
                    Image(
                        painter = painterResource(id = R.drawable.cloudy),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                    )
                } else if (weatherInfo.weather.first().main == "Rain") {
                    Image(
                        painter = painterResource(id = R.drawable.rainy),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                    )
                } else if (weatherInfo.weather.first().main == "Snow") {
                    Image(
                        painter = painterResource(id = R.drawable.snow),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.weather_forecast),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Date: ${weatherInfo.dt_txt}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Weather: ${weatherInfo.weather.first().main}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Description: ${weatherInfo.weather.first().description}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Temperature: ${(weatherInfo.main.temp - 273).toInt()}°C", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

suspend fun getWeatherByCity(city: String?,curLatitude:Double?,curLongitude:Double?): Pair<String,List<Forecast>?> {
    if (Info.userInfo.useCurrent) {
        return Pair("ok", weatherHandler(curLatitude, curLongitude, null))
    }
    if (city !=null) {
        val (latitude, longitude) = getCoordinatesByCity(city)
        return Pair("ok", weatherHandler(latitude, longitude, null))
    } else {
        return Pair("",null)
    }
}

suspend fun getAQI(city: String?,curLatitude:Double?,curLongitude:Double?): Pair<String,Int?> {
    if (Info.userInfo.useCurrent) {
        return Pair("ok", getAQIHandler(curLatitude,curLongitude,null))
    }
    if (city !=null) {
        val (latitude, longitude) = getCoordinatesByCity(city)
        return Pair("ok", getAQIHandler(latitude,longitude,null))
    } else {
        return Pair("",null)
    }
}
