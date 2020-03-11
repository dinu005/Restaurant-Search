package com.sample.restuarant.search.viewmodel

import androidx.lifecycle.LiveData
import com.sample.restuarant.search.model.RestaurantModel
import io.reactivex.Completable

/* @author Dinesh Kumar 
   @creation_date 3/10/2020*/

interface RestaurantViewModel {

    /**
     * Provides a live data, the Activity/Fragment can register for data changes and will be notified
     * when updated
     */
    fun getRestaurantsObservableData(): LiveData<List<RestaurantModel>>

    /**
     * Updates the restaurants list based on the user location.
     */
    fun syncRestaurants(latitude: Double, longitude: Double, radius: Int): Completable
}