package com.sample.restuarant.search.controller

import com.sample.restuarant.search.model.RestaurantModel
import io.reactivex.Single

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

interface RestaurantService {

    /**
     * Provides a list of nearby
     */
    fun getNearByRestaurants(
        latitude: Double,
        longitude: Double,
        radius: Int
    ): Single<List<RestaurantModel>>
}