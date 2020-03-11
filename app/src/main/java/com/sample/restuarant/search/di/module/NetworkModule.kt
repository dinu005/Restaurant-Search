package com.sample.restuarant.search.di.module

import android.content.Context
import com.sample.restuarant.search.controller.RestaurantService
import com.sample.restuarant.search.controller.RestaurantServiceImpl
import com.sample.restuarant.search.di.scope.ApplicationScope
import com.sample.restuarant.search.network.CredentialsInterceptor
import com.sample.restuarant.search.network.ErrorInterceptor
import com.sample.restuarant.search.network.RestaurantApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

@Module
open class NetworkModule {

    @Provides
    fun providesCredentialInterceptor(appContext: Context): CredentialsInterceptor {
        return CredentialsInterceptor(appContext)
    }

    @Provides
    fun providesErrorInterceptor(): ErrorInterceptor {
        return ErrorInterceptor()
    }

    @Provides
    fun providesHttpClient(
        credentialInterceptor: CredentialsInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(credentialInterceptor)
            .addInterceptor(errorInterceptor)
            .build()
    }

    @Provides
    fun providesRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getApiEndPoint())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    fun providesRestaurantApi(retrofit: Retrofit): RestaurantApi {
        return retrofit.create(RestaurantApi::class.java)
    }

    @Provides
    @ApplicationScope
    fun providesRestaurantService(restaurantApi: RestaurantApi): RestaurantService {
        return RestaurantServiceImpl(restaurantApi)
    }

    open fun getApiEndPoint(): String {
        return "https://api.foursquare.com/"
    }

}