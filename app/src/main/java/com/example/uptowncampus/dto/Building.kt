package com.example.uptowncampus.dto

import com.google.gson.annotations.SerializedName

/**
 * A data class representing a building, with a building id and name.
 * @property buildingId The id of the building.
 * @property buildingName The name of the building.
 */
data class Building(@SerializedName("id") var buildingId : Int = 0, var buildingName : String) {
    override fun toString(): String {
        return "$buildingName"
    }
}