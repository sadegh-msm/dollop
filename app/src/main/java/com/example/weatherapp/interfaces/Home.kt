package com.example.weatherapp.interfaces

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weatherapp.MainActivity
import com.example.weatherapp.model.Info
import com.example.weatherapp.model.UserInformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier, title: String, icon: ImageVector? = null) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowHomePage(navHostController: NavHostController, mainActivity: MainActivity) {
    val info by remember { mutableStateOf(UserInformation()) }
    Info.userInfo = info
    Info.userInfo.currentLatitude = Info.viewModel._latitude.value
    Info.userInfo.currentLongitude = Info.viewModel._longitude.value
    val isNetworkAvailable = remember { mutableStateOf(mainActivity.isNetworkAvailable()) }
    var showErrorDialog = remember { mutableStateOf(false) }

    if (!isNetworkAvailable.value) {
        ErrorDialog(onDismiss = {
            if(!isNetworkAvailable.value) {
                navHostController.navigate("home_page")
            }
        }, text = "Connection Error")
    } else {
        Scaffold(
            topBar = { TopAppBar(title = "Weather app", icon = Icons.Default.Home) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CascadingMenu()
                    val context = LocalContext.current
                    AppButton(
                        onClick = {
                            if (isNetworkAvailable.value) {
                                if (Info.userInfo.currentLatitude != null && Info.userInfo.currentLongitude != null) {
                                    Info.userInfo.useCurrent = true
                                    navHostController.navigate("weather_page")
                                }
                            } else {
                                showErrorDialog.value = true
                            }
                        },
                        text = "Use Current Location",
                        icon = Icons.Default.LocationOn
                    )
                    if (showErrorDialog.value) {
                        ErrorDialog(onDismiss = { navHostController.navigate("home_page") }, text = "Connection Error")
                    }
                    AppButton(
                        onClick = {
                            if (isNetworkAvailable.value) {
                                if (Info.userInfo.city.isNotEmpty()) {
                                    navHostController.navigate("weather_page")
                                } else {
                                    Toast.makeText(context, "Select city info", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                showErrorDialog.value = true
                            }
                        },
                        text = "Submit",
                        icon = Icons.Default.Send
                    )
                    if (showErrorDialog.value) {
                        ErrorDialog(onDismiss = { navHostController.navigate("home_page") }, text = "Connection Error")
                    }
                }
            }
        }
    }
}
