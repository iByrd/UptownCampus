package com.example.uptowncampus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.uptowncampus.ui.theme.UptownCampusTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UptownCampusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BuildingName("Android")
                }
            }
        }
    }
}

@Composable
fun BuildingName(name: String) {
    var buildingName by remember { mutableStateOf("") }
    var diningOptions by remember { mutableStateOf("") }
    var activityName by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column {
        OutlinedTextField(
            value = buildingName,
            onValueChange = { buildingName = it },
            label = { Text(stringResource(R.string.buildingName)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = diningOptions,
            onValueChange = { diningOptions = it },
            label = { Text(stringResource(R.string.diningOptions)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text(stringResource(R.string.activityName)) },
            modifier = Modifier.fillMaxWidth()
        )
        Button (
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                Toast.makeText(context, "$buildingName $diningOptions $activityName", Toast.LENGTH_LONG).show()
            }
                )
        {
            Text(text = "Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UptownCampusTheme {
        BuildingName("Android")
    }
}