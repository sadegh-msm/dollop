package com.example.weatherapp.interfaces

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import androidx.compose.runtime.*

@Composable
fun ErrorScreen() {
    var showError by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showError) {
            ErrorMessageCard(onTryAgainClicked = { showError = false })
        }
    }
}

@Composable
fun ErrorMessageCard(onTryAgainClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Use a placeholder for the error icon
            Icon(painter = painterResource(id = R.drawable.hot), contentDescription ="" )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "An error occurred !",
                color = MaterialTheme.colorScheme.error,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "An error occurred, please check your input.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Button(
                    onClick = { /* You can handle dismiss here if needed */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dismiss")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onTryAgainClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(onDismiss: () -> Unit,text:String) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = text)
        },
        text = {
            Text("An error occurred, please check your connection.")
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Try Again")
            }
        },
    )
}