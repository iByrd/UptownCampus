package com.example.uptowncampus.dao

import com.example.uptowncampus.dto.Building
import retrofit2.Call
import retrofit2.http.GET

/**
 *Interface for fetching building data from a remote data source.
 *
 *Retrieves a list of all buildings from the remote data source.
 *@return A [Call] object that can be used to asynchronously retrieve the list of buildings.
 */
interface IBuildingDAO {
    @GET("/~byrdj7/UptownCampus/buildingsJSON.json")
    fun getAllBuildings() : Call<ArrayList<Building>>
}