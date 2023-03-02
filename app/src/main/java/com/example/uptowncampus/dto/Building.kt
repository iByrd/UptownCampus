package com.example.uptowncampus.dto

data class Building(var buildingId : Int = 0, var buildingName : String) {
    override fun toString(): String {
        return buildingName
    }
}