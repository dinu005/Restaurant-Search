package com.sample.restuarant.search.network

import com.sample.restuarant.search.entity.RestaurantResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

interface RestaurantApi {

    @GET("v2/venues/search?query=restaurant")
    fun syncNearByRestaurants(
        @Query("ll") latLong: String,
        @Query("radius") radius: Int,
        @Query("intent") intent: String,
        @Query("v") version: String
    ): Single<RestaurantResponse>

}