package com.example.uptowncampus

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.dto.SavedBuildings
import com.example.uptowncampus.service.BuildingService
import com.example.uptowncampus.service.IBuildingService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.launch
import org.json.JSONException

@Suppress("UNNECESSARY_SAFE_CALL")
class MainViewModel(private var BuildingService : IBuildingService? = null) : ViewModel() {

    internal val newbUILDING = "New Building"
    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()

    var savedBuildings: MutableLiveData<List<SavedBuildings>> = MutableLiveData<List<SavedBuildings>>()
    var selectedSavedBuilding by mutableStateOf(SavedBuildings())
    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()


    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenForSavedBuildings()
    }
    val NEW_BUILDING_NAME = "New Building"
      // MB - I was trying to link data to database but we need to fix how our database is setup
    private fun listenForSavedBuildings() {
        firestore.collection("buildings").addSnapshotListener {
            snapshot, e ->
            //handle error
            if (e != null) {
                Log.w("Listen Failed",e)
                return@addSnapshotListener
            }
            snapshot?.let {
                val allBuildings = ArrayList<SavedBuildings>()
                allBuildings.add(SavedBuildings(buildingName = NEW_BUILDING_NAME ))
                val documents = snapshot.documents
                documents.forEach {
                    val building = it.toObject(SavedBuildings::class.java)
                    building?.let {
                        allBuildings.add(it)
                    }
                }
                savedBuildings.value = allBuildings
            }
        }
    }

    fun fetchBuildings() {
        viewModelScope.launch {
            try{
                buildings.postValue(BuildingService?.fetchBuilding())
            } catch(e: JSONException){
                Log.e("buildingService", "Failed to fetch buildings")
            }
        }
    }

    // -RS- Commented out this function for when the database has been restructured.
//    fun save(studentComment: StudentComment) {
//        val document = if (studentComment.commentId.isEmpty()) {
//            firestore.collection("comments").document()
//        } else {
//            firestore.collection("comments").document(studentComment.commentId)
//        }
//        studentComment.commentId = document.id
//        val handle = document.set(studentComment)
//        handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
//        handle.addOnFailureListener { Log.e("Firebase", "Save failed $it")}
//    }

    fun saveBuilding() {
        val document = if (selectedSavedBuilding?.buildingId.isNullOrEmpty()) {
            firestore.collection("buildings").document()
        } else {
            firestore.collection("buildings").document(selectedSavedBuilding.buildingId)
        }
        selectedSavedBuilding?.buildingId = document.id
        val handle = document.set(selectedSavedBuilding)
        handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
        handle.addOnFailureListener { Log.e("Firebase", "Save failed $it")}
    }
}
