package com.example.uptowncampus

import android.content.ContentValues.TAG
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Home
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.dto.Photo
import com.example.uptowncampus.dto.SavedBuildings
import com.example.uptowncampus.dto.User
import com.example.uptowncampus.ui.theme.UptownCampusTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : ComponentActivity() {

    private var uri: Uri? = null
    private lateinit var currentImagePath: String
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var selectedBuilding: Building? = null
    private val viewModel: MainViewModel by viewModel()
    private var inBuildingName: String = ""
    private var strUri by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.fetchBuildings()
            firebaseUser?.let {
                val user = User(it.uid, "")
                viewModel.user = user
                viewModel.listenForSavedBuildings()
            }
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
    fun TopToolBar() {
        Column {
            TopAppBar(title = {
                Text(
                    text = "Uptown Campus", fontSize = 25.sp, fontWeight = FontWeight.Bold
                )
            }, actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Button(
                        onClick = {
                            signIn()
                        }
                    )
                    {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "User Icon",
                            Modifier.padding(end = 8.dp)
                        )
                        Text(text = "Login")
                    }
                }
            })
        }
    }

    @Composable
    fun BuildingSpinner (savedBuildings : List<SavedBuildings>) {
        var buildingText by remember {(mutableStateOf("Select Building"))}
        var expanded by remember {(mutableStateOf(false))}
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(Modifier
                .padding(22.dp)
                .clickable {
                    expanded = !expanded
                }
                .padding(1.dp),
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
    fun TextFieldWithDropdownUsage(dataIn: List<Building>, label: String = "",
                                   selectedSavedBuilding: SavedBuildings = SavedBuildings(),
                                   leadingIcon: @Composable (() -> Unit)? = null) {
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
            label = label,
            leadingIcon = leadingIcon
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
        label: String = "",
        leadingIcon: @Composable (() -> Unit)? = null
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
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White),
                leadingIcon = { leadingIcon?.invoke()}
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
        savedBuildings: List<SavedBuildings> = ArrayList(),
        selectedSavedBuilding: SavedBuildings = SavedBuildings()
    ) {
        var diningOptions by remember { mutableStateOf("") }
        var activityName by remember { mutableStateOf("") }
        var inComment by remember { mutableStateOf("") }
        val context = LocalContext.current

        Column {
            TopToolBar()
        }

        Column (
            Modifier.padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            BuildingSpinner(savedBuildings = savedBuildings)
            Text ("Search and Add your UC locations", fontSize = 18.sp)
            Row (verticalAlignment = Alignment.CenterVertically) {
                TextFieldWithDropdownUsage(
                    dataIn = buildings,
                    label = stringResource(R.string.buildingName),
                    selectedSavedBuilding = selectedSavedBuilding,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Building Icon",
                            Modifier.padding(end = 8.dp),
                            tint = Color.Black
                        )
                    }
                )
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = diningOptions,
                    onValueChange = { diningOptions = it },
                    label = { Text(stringResource(R.string.diningOptions)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White),
                            leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Fastfood,
                            contentDescription = "Dining Icon",
                            Modifier.padding(end = 8.dp),
                            tint = Color.Black
                        )
                    },
                )
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = activityName,
                    onValueChange = { activityName = it },
                    label = { Text(stringResource(R.string.activityName)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Stadium,
                            contentDescription = "Stadium Icon",
                            Modifier.padding(end = 8.dp),
                            tint = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = Color.White)
                )
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = inComment,
                    onValueChange = { inComment = it },
                    label = { Text(stringResource(R.string.comment)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Comment,
                            contentDescription = "Comment Icon",
                            Modifier.padding(end = 8.dp),
                            tint = Color.Black
                        )
                    },
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
                    contentDescription = "Save Button Icon",
                    Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(R.string.submit))
            }
            Button(
                shape = CutCornerShape(10),
                onClick = {
                    takePhoto()
                }
            ){
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Photo Button Icon",
                    Modifier.padding(end = 8.dp)
                )
                Text(text = "photo")
            }
            AsyncImage(model = strUri, contentDescription = "Building Image")
        }
    }

    private fun takePhoto() {
        if (hasCameraPermission() == PERMISSION_GRANTED && hasExternalStoragePermission() == PERMISSION_GRANTED){
            invokeCamera()
        } else {
            requestMultiplePermissionsLauncher.launch(arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ))
        }
    }

    private fun invokeCamera() {
        val file = createImageFile()
        try {
            uri = FileProvider.getUriForFile(this, "com.example.uptowncampus.fileprovider", file)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            var foo = e.message
        }
        getCameraImage.launch(uri)
    }

    private fun createImageFile() : File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "Building_${timestamp}",
            ".jpg",
            imageDirectory
        ).apply {
            currentImagePath = absolutePath
        }
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
        if (success) {
            Log.i(TAG, "Image Location: $uri")
            strUri = uri.toString()
            val photo = Photo(localUri = uri.toString())
            viewModel.photos.add(photo)
        } else {
            Log.e(TAG, "Image not saved: $uri")
        }
    }

    private val requestMultiplePermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            resultsMap ->
        var permissionGranted = false
        resultsMap.forEach {
            if (it.value == true) {
                permissionGranted = it.value
            } else {
                permissionGranted = false
                return@forEach
            }
        }
        if (permissionGranted) {
            invokeCamera()
        } else {
            Toast.makeText(this, getString(R.string.cameraPermissionDenied), Toast.LENGTH_LONG).show()
        }
    }

    fun hasCameraPermission() = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
    fun hasExternalStoragePermission() = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

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

        signInLauncher.launch(signinIntent)
    }

    private val signInLauncher = registerForActivityResult (
        FirebaseAuthUIActivityResultContract()
            ){
        res -> this.signInResult(res)
    }


    private fun signInResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if(result.resultCode == RESULT_OK){
            firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.let {
                val user = User(it.uid, it.displayName)
                viewModel.user = user
                viewModel.saveUser()
                viewModel.listenForSavedBuildings()
            }
        } else{
            Log.e("MainActivity.kt", "Error logging in" + response?.error?.errorCode)
        }
    }
}