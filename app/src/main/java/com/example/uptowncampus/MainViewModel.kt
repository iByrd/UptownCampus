package com.example.uptowncampus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.dto.StudentComment
import com.example.uptowncampus.service.BuildingService
import com.example.uptowncampus.service.IBuildingService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainViewModel(var buildingService : IBuildingService = BuildingService()) : ViewModel() {

    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()

    private lateinit var firestore: FirebaseFirestore

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun fetchBuildings() {
        viewModelScope.launch {
            var innerBuildings = buildingService.fetchBuilding()
            buildings.postValue(innerBuildings)
        }
    }

    fun save(studentComment: StudentComment) {
        val document = firestore.collection("comments").document(studentComment.commentId ?: "")
        studentComment.commentId = document.id
        document.set(studentComment)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Document saved")
                } else {
                    Log.e("Firebase", "Save failed ${task.exception}")
                }
            }
    }
}

