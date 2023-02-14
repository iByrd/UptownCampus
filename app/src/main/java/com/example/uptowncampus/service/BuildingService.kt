package com.example.uptowncampus.service

import com.example.uptowncampus.RetrofitClientInstance
import com.example.uptowncampus.dao.IBuildingDAO
import com.example.uptowncampus.dto.Building
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class BuildingService {

    suspend fun fetchBuilding() : List<Building>? {
        return withContext(Dispatchers.IO) {
            val service = RetrofitClientInstance.retrofitInstance?.create(IBuildingDAO::class.java)
            val buildings = async {service?.getAllBuildings()}
            var results = buildings.await()?.awaitResponse()?.body()
            return@withContext results
        }
    }
}