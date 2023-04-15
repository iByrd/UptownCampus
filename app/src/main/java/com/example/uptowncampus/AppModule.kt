package com.example.uptowncampus

import com.example.uptowncampus.service.BuildingService
import com.example.uptowncampus.service.IBuildingService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * A module providing dependencies for the application.
 *
 * Provides a [MainViewModel] object for use in the application.
 */

val appModule = module {
    viewModel { MainViewModel(get()) }
    single<IBuildingService> { BuildingService() }
}