package com.example.uptowncampus

import com.example.uptowncampus.service.BuildingService
import com.example.uptowncampus.service.IBuildingService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel(get()) }
    single<IBuildingService> { BuildingService() }
}