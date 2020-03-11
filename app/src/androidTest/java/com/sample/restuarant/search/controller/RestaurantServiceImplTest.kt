package com.sample.restuarant.search.controller

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sample.restuarant.search.R
import com.sample.restuarant.search.RestaurantApplication
import com.sample.restuarant.search.di.DaggerTestApplicationComponent
import com.sample.restuarant.search.di.MockNetworkModule
import com.sample.restuarant.search.di.TestApplicationComponent
import com.sample.restuarant.search.mock.MockApiResponse
import com.sample.restuarant.search.model.RestaurantModel
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class RestaurantServiceImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var application: RestaurantApplication

    @Inject
    lateinit var restaurantService: RestaurantService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)

        application =
            ApplicationProvider.getApplicationContext<Application>() as RestaurantApplication
        val component: TestApplicationComponent = DaggerTestApplicationComponent.builder()
            .application(application)
            .networkModule(MockNetworkModule())
            .create()

        application.injector = component

        component.inject(this)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testFourSquareVenueApiResponse() {
        val mockApiResponse = MockApiResponse()
        val mockServerResponse = MockResponse()
        mockServerResponse.setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockApiResponse.successResponse())

        mockWebServer.enqueue(mockServerResponse)

        val testObserver = TestObserver.create<List<RestaurantModel>>()
        restaurantService.getNearByRestaurants(1.0, -1.0, 20)
            .subscribe(testObserver)

        val mockServerRequest = mockWebServer.takeRequest()
        val requestUrl = mockServerRequest.requestUrl
        val clientId = requestUrl.queryParameter("client_id")
        val clientSecret = requestUrl.queryParameter("client_secret")

        val resources = application.applicationContext.resources

        // assertions
        assertEquals(
            resources.getString(R.string.api_creds_key),
            clientId
        )
        assertEquals(
            resources.getString(R.string.api_creds_secret),
            clientSecret
        )
        testObserver.assertNoErrors()
            .assertComplete()

        val values = testObserver.values().get(0)
        assertEquals(1, values.size)
    }

    @Test
    fun testFourSquareApiFailure() {
        val mockServerResponse = MockResponse()
        mockServerResponse.setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)

        mockWebServer.enqueue(mockServerResponse)

        val testObserver = TestObserver.create<List<RestaurantModel>>()
        restaurantService.getNearByRestaurants(1.0, -1.0, 20)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
            .assertComplete()

        val values = testObserver.values().get(0)
        assertEquals(0, values.size)

    }

}