package com.example.uptowncampus

import com.example.uptowncampus.dto.SavedBuildings
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val buildingsCollection = firestore.collection("buildings")

    fun listenForSavedBuildings(callback: (List<SavedBuildings>) -> Unit) {
        buildingsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                //handle error
                return@addSnapshotListener
            }

            val allBuildings = snapshot?.documents?.mapNotNull { it.toObject(SavedBuildings::class.java) }
            callback(allBuildings.orEmpty())
        }
    }

    fun saveBuilding(building: SavedBuildings, onSuccess: () -> Unit) {
        val document = if (building.buildingId.isNullOrEmpty()) {
            buildingsCollection.document()
        } else {
            buildingsCollection.document(building.buildingId!!)
        }
        building.buildingId = document.id
        val handle = document.set(building)
        handle.addOnSuccessListener {
            onSuccess()
        }
    }

    fun generateNewBuildingId(): String = buildingsCollection.document().id
}
