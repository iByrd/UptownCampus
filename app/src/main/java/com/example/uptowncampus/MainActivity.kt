package com.example.uptowncampus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.PopupProperties
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.ui.theme.UptownCampusTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel<MainViewModel>()
    private var strSelectedData: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.fetchBuildings()
            val buildings by viewModel.buildings.observeAsState(initial = emptyList())
            UptownCampusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BuildingName("Android", buildings)
                }
                // for debugging only
                var foo = buildings
                var i = 1 + 1
            }
        }
    }


    @Composable
    fun TextFieldWithDropdownUsage(dataIn: List<Building>, label: String = "", take: Int = 3) {

        val dropDownOptions = remember { mutableStateOf(listOf<Building>()) }
        val textFieldValue = remember { mutableStateOf(TextFieldValue()) }
        val dropDownExpanded = remember { mutableStateOf(false) }

        fun onDropdownDismissRequest() {
            dropDownExpanded.value = false
        }

        fun onValueChanged(value: TextFieldValue) {
            strSelectedData = value.text
            dropDownExpanded.value = true
            textFieldValue.value = value
            dropDownOptions.value = dataIn.filter {
                it.toString().startsWith(value.text) && it.toString() != value.text
            }.take(take)
        }

        TextFieldWithDropdown(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue.value,
            setValue = ::onValueChanged,
            onDismissRequest = ::onDropdownDismissRequest,
            dropDownExpanded = dropDownExpanded.value,
            list = dropDownOptions.value,
            label = label
        )
    }

    @Composable
    fun TextFieldWithDropdown(
        modifier: Modifier = Modifier,
        value: TextFieldValue,
        setValue: (TextFieldValue) -> Unit,
        onDismissRequest: () -> Unit,
        dropDownExpanded: Boolean,
        list: List<Building>,
        label: String = ""
    ) {
        Box(modifier) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused)
                            onDismissRequest()
                    },
                value = value,
                onValueChange = setValue,
                label = { Text(label) },
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            DropdownMenu(
                expanded = dropDownExpanded,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                onDismissRequest = onDismissRequest
            ) {
                list.forEach { text ->
                    DropdownMenuItem(onClick = {
                        setValue(
                            TextFieldValue(
                                text.toString(),
                                TextRange(text.toString().length)
                            )
                        )

                    }) {
                        Text(text = text.toString())
                    }
                }
            }
        }
    }

    @Composable
    fun BuildingName(name: String, buildings : List<Building> = ArrayList<Building>()) {
        var buildingName by remember { mutableStateOf("") }
        var diningOptions by remember { mutableStateOf("") }
        var activityName by remember { mutableStateOf("") }
        val context = LocalContext.current
        Column {
            TextFieldWithDropdownUsage(dataIn = buildings, stringResource(R.string.buildingNameLabel))
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
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    Toast.makeText(
                        context,
                        "$buildingName $diningOptions $activityName",
                        Toast.LENGTH_LONG
                    ).show()
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
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                BuildingName("Android")
            }

        }
    }
}

