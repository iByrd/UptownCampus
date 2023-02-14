package com.example.uptowncampus.dao

import com.example.uptowncampus.dto.Building
import retrofit2.Call
import retrofit2.http.GET

interface IBuildingDAO {

    @GET("/iByrd/UptownCampus/tree/TDD_BDD_ParseJSONwithRetroFit/buildingsJSON.txt")
    fun getAllBuildings() : Call<ArrayList<Building>>
}