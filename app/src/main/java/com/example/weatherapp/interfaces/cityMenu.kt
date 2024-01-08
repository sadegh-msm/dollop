package com.example.weatherapp.interfaces

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.Info
import androidx.compose.material.icons.filled.*


@Composable
fun CascadingMenu() {
    var selectedState by remember { mutableStateOf<String?>(null) }
    var selectedCity by remember { mutableStateOf<String?>(null) }

    val states = listOf("Tehran", "Yazd", "Khoozestan", "Khorasan Razavi", "Esfahan")
    val citiesByState = mapOf(
        "Tehran" to listOf("Tehran", "Qods", "Pakdasht"),
        "Khorasan Razavi" to listOf("Mashhad", "Sabzevar", "Sangan"),
        "Esfahan" to listOf("Esfahan", "Natanz", "Khur"),
        "Yazd" to listOf("Ardakan", "Taft", "Yzad"),
        "Khoozestan" to listOf("Ahvaz", "Dezful", "Khorram shahr"),
    )

    Column(modifier = Modifier.padding(16.dp)) {
        CascadingDropdown(
            items = states,
            selectedItem = selectedState,
            onItemSelected = { state ->
                selectedState = state
                selectedCity = null
                Info.userInfo.state = selectedState as String
            },
            label = "Select State",
            onClick = {},
            icon = Icons.Default.ArrowDropDown
        )
        Spacer(modifier = Modifier.height(16.dp))
        val context = LocalContext.current
        CascadingDropdown(
            items = citiesByState[selectedState ?: ""] ?: emptyList(),
            selectedItem = selectedCity,
            onItemSelected = { city ->
                selectedCity = city
                Info.userInfo.city = selectedCity as String
            },
            label = "Select City",
            onClick = {
                val cities = if (selectedState != null) citiesByState[selectedState] ?: emptyList() else emptyList()
                if(cities.isEmpty()){
                    Toast.makeText(context, "Select state first", Toast.LENGTH_SHORT).show()
                }
            },
            icon = Icons.Default.Place
        )
    }
}

@Composable
fun CascadingDropdown(
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String) -> Unit,
    label: String,
    onClick: () -> Unit,
    icon: ImageVector
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
        Text(text = label)
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, MaterialTheme.shapes.small),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedItem ?: "Select an item",
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        text = { Text(text = item) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsernameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Username",
    placeholder: String = "Enter your username",
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 10.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp)
            ),
        leadingIcon = {
            Icon(
                Icons.Default.Person,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None
    )
}