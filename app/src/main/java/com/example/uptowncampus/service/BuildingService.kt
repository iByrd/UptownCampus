package com.example.uptowncampus.service

import com.example.uptowncampus.RetrofitClientInstance
import com.example.uptowncampus.dao.IBuildingDAO
import com.example.uptowncampus.dto.Building
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

/**
 * A service for fetching building data from a remote data source.
 *
 * Fetches a list of buildings from the remote data source.
 * @return A list of [Building] objects, or null if the fetch fails.
 */
interface IBuildingService {
    suspend fun fetchBuilding() : List<Building>?
}

class BuildingService : IBuildingService {
    override suspend fun fetchBuilding() : List<Building>? {
        return withContext(Dispatchers.IO) {
            val service = RetrofitClientInstance.retrofitInstance?.create(IBuildingDAO::class.java)
            val buildings = async { service?.getAllBuildings() }
            return@withContext buildings.await()?.awaitResponse<ArrayList<Building>>()?.body()
        }
    }
}