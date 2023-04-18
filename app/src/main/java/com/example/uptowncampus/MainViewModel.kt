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
import com.example.uptowncampus.dto.StudentComment
import com.example.uptowncampus.dto.User
import com.example.uptowncampus.ui.theme.service.BuildingService
import com.example.uptowncampus.ui.theme.service.IBuildingService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import org.json.JSONException

class MainViewModel(var buildingService : IBuildingService = BuildingService()) : ViewModel() {

    internal val NEW_BUILDING = "New Building"
    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()
    var savedBuildings: MutableLiveData<List<SavedBuildings>> = MutableLiveData<List<SavedBuildings>>()
    var selectedSavedBuilding by mutableStateOf(SavedBuildings())
    var user : User? = null

    private lateinit var firestore : FirebaseFirestore

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

    }

      // MB - I was trying to link data to database but we need to fix how our database is setup
    fun listenForSavedBuildings() {
          user?.let {
              user ->
              firestore.collection("users").document(user.uid).collection("buildings")
                  .addSnapshotListener { snapshot, e ->
                      //handle error
                      if (e != null) {
                          Log.w("Listen Failed", e)
                          return@addSnapshotListener
                      }
                      snapshot?.let {
                          val allBuildings = ArrayList<SavedBuildings>()
                          allBuildings.add(SavedBuildings(buildingName = NEW_BUILDING))
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
    }

    fun fetchBuildings() {
        viewModelScope.launch {
            try{
                buildings.postValue(buildingService.fetchBuilding())
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
        user?.let {
            user ->
            val document =
                if (selectedSavedBuilding.buildingId.isEmpty() || selectedSavedBuilding.buildingId == null) {
                    firestore.collection("users").document(user.uid).collection("buildings").document()
                } else {
                    firestore.collection("users").document(user.uid).collection("buildings").document(selectedSavedBuilding.buildingId)
                }
            selectedSavedBuilding.buildingId = document.id
            val handle = document.set(selectedSavedBuilding)
            handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
            handle.addOnFailureListener { Log.e("Firebase", "Save failed $it") }
        }
    }

    fun saveUser () {
        user?.let {
            user ->
            val handle = firestore.collection("users").document(user.uid).set(user)
            handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
            handle.addOnFailureListener { Log.e("Firebase", "Save failed $it") }
        }
    }
}