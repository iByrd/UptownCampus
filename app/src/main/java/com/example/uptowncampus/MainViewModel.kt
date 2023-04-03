package com.example.uptowncampus

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.*
import com.example.uptowncampus.service.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.launch
import org.json.JSONException
import com.example.uptowncampus.FirestoreRepository

class MainViewModel(private val buildingService: BuildingService, private val firestoreRepository: FirestoreRepository) : ViewModel() {

    internal val NEW_BUILDING = "New Building"
    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()

    var savedBuildings: MutableLiveData<List<SavedBuildings>> = MutableLiveData<List<SavedBuildings>>()
    var selectedSavedBuilding by mutableStateOf(SavedBuildings())

    private lateinit var firestore : FirebaseFirestore

    init {
        listenForSavedBuildings()
    }

      // MB - I was trying to link data to database but we need to fix how our database is setup
    private fun listenForSavedBuildings() {
          firestoreRepository.listenForSavedBuildings { allBuildings ->
              allBuildings.add(0, SavedBuildings(buildingName = NEW_BUILDING))
              savedBuildings.value = allBuildings
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
    
    fun saveBuilding() {
        selectedSavedBuilding.buildingId = selectedSavedBuilding.buildingId.takeUnless { it.isNullOrEmpty() } ?: firestoreRepository.generateNewBuildingId()
        firestoreRepository.saveBuilding(selectedSavedBuilding) {
            Log.d("Firebase", "Document Saved")
        }
    }
}