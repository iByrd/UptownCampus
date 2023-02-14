package com.example.uptowncampus.dao

import com.example.uptowncampus.dto.Building
import retrofit2.Call
import retrofit2.http.GET

interface IBuildingDAO {

    @GET("/perl/mobile/viewplantsjsonarray.pl")
    fun getAllBuildings() : Call<ArrayList<Building>>
}