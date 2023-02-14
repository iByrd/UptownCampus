package com.example.uptowncampus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.service.BuildingService
import com.example.uptowncampus.service.IBuildingService
import kotlinx.coroutines.launch

class MainViewModel(var buildingService : IBuildingService = BuildingService()) : ViewModel() {

    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()

    fun fetchBuildings() {
        viewModelScope.launch {
            var innerBuildings = buildingService.fetchBuilding()
            buildings.postValue(innerBuildings)
        }
    }
}