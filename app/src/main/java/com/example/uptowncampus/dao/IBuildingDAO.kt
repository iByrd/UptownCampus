package com.example.uptowncampus.dao

import com.example.uptowncampus.dto.Building
import retrofit2.Call
import retrofit2.http.GET

interface IBuildingDAO {
    @GET("/~byrdj7/UptownCampus/buildingsJSON.json")
    fun getAllBuildings(): Call<List<Building>>
}
