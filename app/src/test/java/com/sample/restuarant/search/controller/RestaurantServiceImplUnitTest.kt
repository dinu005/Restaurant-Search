package com.sample.restuarant.search.controller

import com.sample.restuarant.search.entity.RestaurantResponse
import com.sample.restuarant.search.mock.MockEntities
import com.sample.restuarant.search.model.RestaurantModel
import com.sample.restuarant.search.network.RestaurantApi
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class RestaurantServiceImplUnitTest {

    @Mock
    lateinit var restaurantApi: RestaurantApi

    @InjectMocks
    lateinit var restaurantService: RestaurantServiceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testRestaurantServiceForSuccess() {
        val testData = MockEntities().getRestaurantEntities()
        val apiResponseSingle = Single.just(testData)
        Mockito.`when`(
            restaurantApi.syncNearByRestaurants(
                anyString(),
                anyInt(),
                anyString(),
                anyString()
            )
        ).thenReturn(apiResponseSingle)

        val testObserver = TestObserver.create<List<RestaurantModel>>()
        restaurantService.getNearByRestaurants()
            .subscribe(testObserver)

        testObserver.assertNoErrors()
            .assertComplete()

        val result = testObserver.values().get(0)
        assertEquals(testData.response.venue.size, result.size)
    }

    @Test
    fun testRestaurantServiceForSuccessWithEmptyCategory() {
        val testData = MockEntities().getRestaurantEntitiesWithEmptyCategory()
        val apiResponseSingle = Single.just(testData)
        Mockito.`when`(
            restaurantApi.syncNearByRestaurants(
                anyString(),
                anyInt(),
                anyString(),
                anyString()
            )
        ).thenReturn(apiResponseSingle)

        val testObserver = TestObserver.create<List<RestaurantModel>>()
        restaurantService.getNearByRestaurants()
            .subscribe(testObserver)

        testObserver.assertNoErrors()
            .assertComplete()

        val result = testObserver.values().get(0)
        assertEquals(testData.response.venue.size, result.size)
    }

    @Test
    fun testRestaurantServiceForSuccessWithEmptyAddress() {
        val testData = MockEntities().getRestaurantEntitiesWithEmptyAddress()
        val apiResponseSingle = Single.just(testData)
        Mockito.`when`(
            restaurantApi.syncNearByRestaurants(
                anyString(),
                anyInt(),
                anyString(),
                anyString()
            )
        ).thenReturn(apiResponseSingle)

        val testObserver = TestObserver.create<List<RestaurantModel>>()
        restaurantService.getNearByRestaurants()
            .subscribe(testObserver)

        testObserver.assertNoErrors()
            .assertComplete()

        val result = testObserver.values().get(0)
        assertEquals(testData.response.venue.size, result.size)
    }

    @Test
    fun testRestaurantServiceForSuccessWithEmptyResponse() {
        val testData = MockEntities().getEmptyRestaurantListResponse()
        val apiResponseSingle = Single.just(testData)
        Mockito.`when`(
            restaurantApi.syncNearByRestaurants(
                anyString(),
                anyInt(),
                anyString(),
                anyString()
            )
        ).thenReturn(apiResponseSingle)

        val testObserver = TestObserver.create<List<RestaurantModel>>()
        restaurantService.getNearByRestaurants()
            .subscribe(testObserver)

        testObserver.assertNoErrors()
            .assertComplete()

        val result = testObserver.values().get(0)
        assertEquals(testData.response.venue.size, result.size)
    }

    @Test
    fun testRestaurantServiceWithError() {
        val apiResponseSingle = Single.error<RestaurantResponse>(RuntimeException())
        Mockito.`when`(
            restaurantApi.syncNearByRestaurants(
                anyString(),
                anyInt(),
                anyString(),
                anyString()
            )
        ).thenReturn(apiResponseSingle)

        val testObserver = TestObserver.create<List<RestaurantModel>>()
        restaurantService.getNearByRestaurants()
            .subscribe(testObserver)

        testObserver.assertNoErrors()
            .assertComplete()

        val result = testObserver.values().get(0)
        assertEquals(0, result.size)
    }

}