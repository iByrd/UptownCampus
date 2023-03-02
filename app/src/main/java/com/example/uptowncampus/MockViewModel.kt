package com.example.uptowncampus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.service.BuildingService
import com.example.uptowncampus.service.IBuildingService
import kotlinx.coroutines.launch
import org.json.JSONException

class MockViewModel(var buildingService : IBuildingService = BuildingService()) : ViewModel() {

    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()

    fun fetchBuildings() {
        viewModelScope.launch {
            try{
                buildings.postValue(buildingService.fetchBuilding())
            } catch(e: JSONException){
                Log.e("buildingService", "Failed to fetch buildings")
            }
        }
    }
}