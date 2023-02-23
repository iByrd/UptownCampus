package com.example.uptowncampus

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.service.BuildingService
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class BuildingTests {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var buildingService : BuildingService
    var allBuildings : List<Building>? = ArrayList<Building>()

    @MockK
    lateinit var mockBuildingService : BuildingService

    @Test
    fun `Given building data is available when I search for Teacher then I should receive Teacher Dyer` () = runTest {
        givenBuildingServiceIsInitialized()
        whenBuildingDataIsReadAndParsed()
        thenTheBuildingCollectionShouldContainTeacherDyer()
    }

    private fun givenBuildingServiceIsInitialized() {
        buildingService = BuildingService()
    }

    private suspend fun whenBuildingDataIsReadAndParsed() {
        allBuildings = buildingService.fetchBuilding()
    }

    private fun thenTheBuildingCollectionShouldContainTeacherDyer() {
        assertNotNull(allBuildings)
        assertTrue(allBuildings!!.isNotEmpty())
        var containsTeacher = false
        allBuildings!!.forEach {
            if (it.buildingName.equals("Teachers-Dyer Complex")) {
                    containsTeacher = true
                }
        }
        assertTrue(containsTeacher)
    }

    @Test
    fun `given a view model with live data when populated with buildings then results show Teachers-Dyer` () {
        givenViewModelIsInitializedUsingMockData()
        whenBuildingServiceFetchBuildingsInvoked()
        thenResultsShouldContainTeacherDyer()
    }

    private fun givenViewModelIsInitializedUsingMockData() {
        //Will contain the building objects
        val buildings = ArrayList<Building>()

        //Hardcoded building objects
        buildings.add(Building(1, "Teachers-Dyer"))
        buildings.add(Building(2, "College of Law"))
        buildings.add(Building(3, "Blegen Library"))
        buildings.add(Building(4, "University Pavilion"))

        coEvery {mockBuildingService.fetchBuilding()} returns buildings

        mvm = MainViewModel()
        mvm.buildingService = mockBuildingService

    }

    private fun whenBuildingServiceFetchBuildingsInvoked() {

    }

    private fun thenResultsShouldContainTeacherDyer() {
        TODO("Not yet implemented")
    }
}