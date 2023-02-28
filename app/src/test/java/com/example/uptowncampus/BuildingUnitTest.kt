package com.example.uptowncampus

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.service.BuildingService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BuildingUnitTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var mainViewModel : MockViewModel

    @MockK
    lateinit var mockBuildingService : BuildingService

    private val mainThreadSurrogate = newSingleThreadContext("Main Thread")

    @Before
    fun initMocksAndMainThread() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
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

        mainViewModel = MockViewModel(buildingService = mockBuildingService)
    }

    private fun whenBuildingServiceFetchBuildingsInvoked() {
        //retrieves data
        mainViewModel.fetchBuildings()
    }

    private fun thenResultsShouldContainTeacherDyer() {
        //capture results
        var allBuildings : List<Building>? = ArrayList<Building>()
        val latch = CountDownLatch(1)
        val observer = object : Observer<List<Building>> {
            override fun onChanged(buildingsRecieved: List<Building>?) {
                allBuildings = buildingsRecieved
                latch.countDown()
                mainViewModel.buildings.removeObserver(this)
            }
        }
        mainViewModel.buildings.observeForever(observer)
        //waits for latch countdown to hit 0
        latch.await(10, TimeUnit.SECONDS)

        TestCase.assertNotNull(allBuildings)
        TestCase.assertTrue(allBuildings!!.isNotEmpty())
        var containsTeacher = false
        allBuildings!!.forEach{
            if (it.buildingName.equals("Teachers-Dyer")) {
                containsTeacher = true
            }
        }
        TestCase.assertTrue(containsTeacher)
    }
}