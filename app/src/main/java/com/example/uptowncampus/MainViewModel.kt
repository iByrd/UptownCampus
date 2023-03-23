package com.example.uptowncampus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.dto.SavedBuildings
import com.example.uptowncampus.dto.StudentComment
import com.example.uptowncampus.service.BuildingService
import com.example.uptowncampus.service.IBuildingService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.launch
import org.json.JSONException

class MainViewModel(var buildingService : IBuildingService = BuildingService()) : ViewModel() {
    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()

    var savedBuildings: MutableLiveData<List<SavedBuildings>> = MutableLiveData<List<SavedBuildings>>()

    private lateinit var firestore : FirebaseFirestore

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenForSavedBuildings()
    }

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
                buildings.postValue(buildingService.fetchBuilding())
            } catch(e: JSONException){
                Log.e("buildingService", "Failed to fetch buildings")
            }
        }
    }

    fun save(studentComment: StudentComment) {
        val document = if (studentComment.commentId.isEmpty()) {
            firestore.collection("comments").document()
        } else {
            firestore.collection("comments").document(studentComment.commentId)
        }
        studentComment.commentId = document.id
        val handle = document.set(studentComment)
        handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
        handle.addOnFailureListener { Log.e("Firebase", "Save failed $it")}
    }

    fun saveBuilding(building: SavedBuildings) {
        val document = if (building.buildingId.isEmpty()) {
            firestore.collection("buildings").document()
        } else {
            firestore.collection("buildings").document(building.buildingId)
        }
        building.buildingId = document.id
        val handle = document.set(building)
        handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
        handle.addOnFailureListener { Log.e("Firebase", "Save failed $it")}
    }
}