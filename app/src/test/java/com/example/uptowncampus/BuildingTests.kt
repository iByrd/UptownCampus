package com.example.uptowncampus

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.service.BuildingService
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
            if (it.buildingName.contains("teacher")) {
                    containsTeacher = true
                }
        }
        assertTrue(containsTeacher)
    }
}