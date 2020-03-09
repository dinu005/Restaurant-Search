package com.sample.restuarant.search.controller

import io.reactivex.Completable

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

interface RestaurantService {

    /**
     * Provides a list of nearby
     */
    fun getNearByRestaurants(): Completable
}