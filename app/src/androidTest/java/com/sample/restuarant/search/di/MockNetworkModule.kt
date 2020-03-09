package com.sample.restuarant.search.di

import com.sample.restuarant.search.di.module.NetworkModule
import dagger.Module
import okhttp3.mockwebserver.MockWebServer

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

@Module
class MockNetworkModule : NetworkModule() {

    override fun getApiEndPoint(): String {
        return MockWebServer().url("http://127.0.0.1:8080/").toString()
    }
}