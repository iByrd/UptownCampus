package com.example.uptowncampus

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.uptowncampus.dto.Building
import com.example.uptowncampus.service.BuildingService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
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
        //retrieves data
        mvm.fetchBuildings()
    }

    private fun thenResultsShouldContainTeacherDyer() {
        //capture results
        var allBuildings : List<Building>? = ArrayList<Building>()
        var latch = CountDownLatch(1)
        val observer = object : Observer<List<Building>> {
            override fun onChanged(buildingsRecieved: List<Building>?) {
                allBuildings = buildingsRecieved
                latch.countDown()
                mvm.buildings.removeObserver(this)
            }
        }
        mvm.buildings.observeForever(observer)
        //waits for latch countdown to hit 0
        latch.await(10, TimeUnit.SECONDS)

        assertNotNull(allBuildings)
        assertTrue(allBuildings!!.isNotEmpty())
        var containsTeacher = false
        allBuildings!!.forEach{
            if (it.buildingName.equals("Teachers-Dyer")) {
                containsTeacher = true
            }
        }
        assertTrue(containsTeacher)
    }
}