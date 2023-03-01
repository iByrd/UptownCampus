package com.example.uptowncampus

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.service.BuildingService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.rules.TestRule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
        assertTrue(Building(1,"Teachers-Dyer Complex") in allBuildings!!)
    }
}