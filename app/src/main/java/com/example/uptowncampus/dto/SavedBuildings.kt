package com.example.uptowncampus.dto

/**
 * A data class representing a SavedBuilding, with a building id and name.
 *
 * Used to store buildings into Firestore database.
 * @property buildingId The id of the building.
 * @property buildingName The name of the building.
 */
data class SavedBuildings (var buildingId : String = "", var buildingName : String = "") {
    override fun toString(): String {
        return "$buildingName"
}}