package com.example.uptowncampus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.service.BuildingService
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var buildings : MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()
    var buildingService : BuildingService = BuildingService()

    fun fetchBuilding() {
        viewModelScope.launch {
            var innerBuildings = buildingService.fetchBuilding()
            buildings.postValue(innerBuildings)
        }
    }
}