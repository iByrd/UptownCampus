package com.example.uptowncampus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.dto.StudentComment
import com.example.uptowncampus.ui.theme.UptownCampusTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private var selectedBuilding: Building? = null
    private val viewModel: MainViewModel by viewModel()
    private var inBuildingName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.fetchBuildings()
            val buildings by viewModel.buildings.observeAsState(initial = emptyList())
            // dummy data for testing only - NEED TO REMOVE LATER
            val savedBuildings = ArrayList<Building>()
            savedBuildings.add(Building(buildingName = "Nippert Stadium"))
            savedBuildings.add(Building(buildingName = "Rec Center"))
            savedBuildings.add(Building(buildingName = "Fifth Third Arena"))
            UptownCampusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BuildingName(buildings, savedBuildings)
                }
            }
        }
    }

    @Composable
    fun BuildingSpinner (savedBuildings : List<Building>) {
        var buildingText by remember {(mutableStateOf("Building Collection"))}
        var expanded by remember {(mutableStateOf(false))}
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(Modifier
                .padding(24.dp)
                .clickable {
                    expanded = !expanded
                }
                .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = buildingText, fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp), fontWeight = FontWeight.Bold)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    savedBuildings.forEach {
                        building -> DropdownMenuItem(onClick = {
                            expanded = false
                            buildingText = building.toString()
                            selectedBuilding = building
                    }) {
                            Text(text = building.toString())
                    }
                    }
                }
            }
        }
    }

    @Composable
    fun TextFieldWithDropdownUsage(dataIn: List<Building>, label: String = "") {
        val dropDownOptions = remember { mutableStateOf(listOf<Building>()) }
        val textFieldValue = remember { mutableStateOf(TextFieldValue()) }
        val dropDownExpanded = remember { mutableStateOf(false) }

        fun onDropdownDismissRequest() {
            dropDownExpanded.value = false
        }

        fun onValueChanged(value: TextFieldValue) {
            inBuildingName = value.text
            dropDownExpanded.value = true
            textFieldValue.value = value
            dropDownOptions.value = dataIn.filter {
                it.toString().contains(value.text,true) && it.toString() != value.text
            }.sortedBy { it.toString().indexOf(value.text, 0, true) }.take(3)
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
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White)
            )
            DropdownMenu(
                expanded = dropDownExpanded,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
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
                        selectedBuilding = text
                    }) {
                        Text(text = text.toString())
                    }
                }
            }
        }
    }

    @Composable
    fun BuildingName(buildings : List<Building> = ArrayList(), savedBuildings : List<Building> = ArrayList<Building>()) {
        var diningOptions by remember { mutableStateOf("") }
        var activityName by remember { mutableStateOf("") }
        var inComment by remember { mutableStateOf("") }
        val context = LocalContext.current
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            BuildingSpinner(savedBuildings = savedBuildings)
            TextFieldWithDropdownUsage(dataIn = buildings, stringResource(R.string.buildingName))
            OutlinedTextField(
                value = diningOptions,
                onValueChange = { diningOptions = it },
                label = { Text(stringResource(R.string.diningOptions)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White)
            )
            OutlinedTextField(
                value = activityName,
                onValueChange = { activityName = it },
                label = { Text(stringResource(R.string.activityName)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White)
            )
            OutlinedTextField(
                value = inComment,
                onValueChange = { inComment = it},
                label = { Text(stringResource(R.string.comment))},
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White)
            )
            Button(
                shape = CutCornerShape(10),
                onClick = {
                    val studentComment = StudentComment().apply {
                        buildingName = inBuildingName
                        buildingId = selectedBuilding?.let {
                            it.buildingId
                        } ?: 0
                        commentContent = inComment
                    }
                    viewModel.save(studentComment)
                    Toast.makeText(
                        context,
                        "$inBuildingName $diningOptions $activityName",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
            {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Localized description",
                    Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(R.string.submit))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        UptownCampusTheme {
            BuildingName()
        }
    }
}