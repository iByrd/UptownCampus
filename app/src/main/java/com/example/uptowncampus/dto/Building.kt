package com.example.uptowncampus.dto

import com.google.gson.annotations.SerializedName

data class Building(val id: Int = 0, val buildingName: String) {
    override fun toString() = buildingName
}
