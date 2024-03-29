package com.example.uptowncampus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.ui.theme.service.BuildingService
import com.example.uptowncampus.ui.theme.service.IBuildingService
import kotlinx.coroutines.launch

class MockViewModel(var buildingService : IBuildingService = BuildingService()) : ViewModel() {

    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()

    fun fetchBuildings() {
        viewModelScope.launch {
            var innerBuildings = buildingService.fetchBuilding()
            buildings.postValue(innerBuildings)
        }
    }
}