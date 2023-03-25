package com.example.uptowncampus

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Stadium
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.dto.SavedBuildings
import com.example.uptowncampus.ui.theme.UptownCampusTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private var user: FirebaseUser? = null
    private var selectedBuilding: Building? = null
    private val viewModel: MainViewModel by viewModel<MainViewModel>()
    private var inBuildingName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.fetchBuildings()
            val buildings by viewModel.buildings.observeAsState(initial = emptyList())
            val savedBuildings by viewModel.savedBuildings.observeAsState(initial = emptyList())
            UptownCampusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BuildingName(buildings, savedBuildings, viewModel.selectedSavedBuilding)
                }
            }
        }
    }

    @Composable
    fun BuildingSpinner (savedBuildings : List<SavedBuildings>) {
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

                        if (building.buildingName == viewModel.NEW_BUILDING) {
                            // for new buildings to be added to the database
                            buildingText = ""
                            building.buildingName = ""
                        } else {
                            // for existing buildings, to prevent duplication
                            buildingText = building.toString()
                            selectedBuilding = Building(buildingId = 0, buildingName = building.buildingName)
                            inBuildingName = building.buildingName
                        }
                        viewModel.selectedSavedBuilding = building
                    }) {
                            Text(text = building.toString())
                    }
                    }
                }
            }
        }
    }

    @Composable
    fun TextFieldWithDropdownUsage(dataIn: List<Building>, label: String = "", selectedSavedBuilding: SavedBuildings = SavedBuildings()) {
        val dropDownOptions = remember { mutableStateOf(listOf<Building>()) }
        val textFieldValue = remember(selectedSavedBuilding.buildingId) { mutableStateOf(TextFieldValue(selectedSavedBuilding.buildingName)) }
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
    fun BuildingName(
        buildings: List<Building> = ArrayList(),
        savedBuildings: List<SavedBuildings> = ArrayList<SavedBuildings>(),
        selectedSavedBuilding: SavedBuildings = SavedBuildings()
    ) {
        var diningOptions by remember { mutableStateOf("") }
        var activityName by remember { mutableStateOf("") }
        var inComment by remember { mutableStateOf("") }
        val context = LocalContext.current
        Column (
            Modifier.padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            BuildingSpinner(savedBuildings = savedBuildings)
            Text ("Search and Add your UC locations", fontSize = 18.sp)
            Row (verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Localized description",
                    Modifier.padding(end = 8.dp),
                )
                TextFieldWithDropdownUsage(
                    dataIn = buildings,
                    label = stringResource(R.string.buildingName),
                    selectedSavedBuilding = selectedSavedBuilding
                )
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Fastfood,
                    contentDescription = "Localized description",
                    Modifier.padding(end = 8.dp),
                )
                OutlinedTextField(
                    value = diningOptions,
                    onValueChange = { diningOptions = it },
                    label = { Text(stringResource(R.string.diningOptions)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White)
                )
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Stadium,
                    contentDescription = "Localized description",
                    Modifier.padding(end = 8.dp),
                )
                OutlinedTextField(
                    value = activityName,
                    onValueChange = { activityName = it },
                    label = { Text(stringResource(R.string.activityName)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White)
                )
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Comment,
                    contentDescription = "Localized description",
                    Modifier.padding(end = 8.dp),
                )
                OutlinedTextField(
                    value = inComment,
                    onValueChange = { inComment = it },
                    label = { Text(stringResource(R.string.comment)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White)
                )
            }
            Button(
                shape = CutCornerShape(10),
                onClick = {
                    selectedSavedBuilding.apply {
                        buildingName = inBuildingName
                    }
                   /* val studentComment = StudentComment().apply {
                        commentContent = inComment
                    }*/
                    //viewModel.save(studentComment)
                    viewModel.saveBuilding()
                    Toast.makeText(
                        context,
                        "$inBuildingName $diningOptions $activityName",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
            {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Localized description",
                    Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(R.string.submit))
            }
            Button(
                onClick = {
                    signIn()
                }
            )
            {
                Text(text = "Logon")
            }
        }
    }



    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        UptownCampusTheme {
            BuildingName(selectedSavedBuilding = viewModel.selectedSavedBuilding)
        }
    }
    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        val signinIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }


    private fun signInResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if(result.resultCode == RESULT_OK){
            user = FirebaseAuth.getInstance().currentUser
        } else{
            Log.e("MainActivity.kt", "Error logging in" + response?.error?.errorCode)
        }
    }
}