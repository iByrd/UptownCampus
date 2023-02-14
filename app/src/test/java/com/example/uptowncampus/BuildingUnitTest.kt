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

    // make unit test run together. fails without this
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    // initialize view model
    lateinit var mainViewModel : MainViewModel

    // initialize mock of BuildingService
    @MockK
    lateinit var mockBuildingService : BuildingService

    private val mainThreadSurrogate = newSingleThreadContext("Main Thread")

    @Before
    fun initMocksAndMainThread() {
        MockKAnnotations.init(this)
        Dispatchers.setMain((mainThreadSurrogate))
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    // test based on GWT in requirements document
    @Test
    fun `given a view model with live data when populated with buildings then results show Teachers-Dyer` () {
        givenViewModelIsInitializedUsingMockData()
        whenBuildingServiceFetchBuildingsInvoked()
        thenResultsShouldContainTeacherDyer()
    }

    private fun givenViewModelIsInitializedUsingMockData() {
        // variable to contain building objects
        val buildings = ArrayList<Building>()
        // some hard-coded values to use as mock
        buildings.add(Building(1, "Teachers-Dyer"))
        buildings.add(Building(2, "College of Law"))
        buildings.add(Building(3, "Blegen Library"))
        buildings.add(Building(4, "University Pavilion"))

        // returns the mock data
        coEvery { mockBuildingService.fetchBuilding() } returns buildings

        // initialize MVM and set to mock data
        mainViewModel = MainViewModel()
        mainViewModel.buildingService = mockBuildingService
    }

    private fun whenBuildingServiceFetchBuildingsInvoked() {
        // go get the data
        mainViewModel.fetchBuildings()
    }

    private fun thenResultsShouldContainTeacherDyer() {
        // capture results
        var allBuildings : List<Building>? = ArrayList<Building>()
        // use latch so the program waits while data is received
        val latch = CountDownLatch(1)
        val observer = object : Observer<List<Building>> {
            override fun onChanged(buildingsReceived: List<Building>?) {
                // data received
                allBuildings = buildingsReceived
                // count down lock so program will proceed
                latch.countDown()
                // stop observing
                mainViewModel.buildings.removeObserver(this)
            }
        }
        mainViewModel.buildings.observeForever(observer)
        // waits for latch or timeout to proceed
        latch.await(10, TimeUnit.SECONDS)

        // check the data for expected result
        TestCase.assertNotNull(allBuildings)
        TestCase.assertTrue(allBuildings!!.isNotEmpty())
        var containsTeacher = false
        allBuildings!!.forEach {
            if (it.buildingName.equals("Teachers-Dyer")) {
                containsTeacher = true
            }
        }
        // true if match, otherwise false
        TestCase.assertTrue(containsTeacher)
    }
}