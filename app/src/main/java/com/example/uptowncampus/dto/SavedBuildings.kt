package com.example.uptowncampus.dto

// This data class is used to store buildings into the Firestore database.
data class SavedBuildings (var buildingId : String = "", var buildingName : String = "") {
    override fun toString(): String {
        return "$buildingName"
}}