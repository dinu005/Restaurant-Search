package com.sample.restuarant.search.controller

import android.util.Log
import com.sample.restuarant.search.entity.Restaurant
import com.sample.restuarant.search.network.RestaurantApi
import io.reactivex.Completable

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

class RestaurantServiceImpl(private val restaurantApi: RestaurantApi) : RestaurantService {

    private val tag = "RestaurantServiceImpl"

    override fun getNearByRestaurants(): Completable {
        return restaurantApi.syncNearByRestaurants(
            "40.74224,-73.99386", 250,
            "browse", "20200301"
        )
            .flatMapCompletable { restaurantList -> transformResult(restaurantList.response.venue) }
            .doOnError { error -> Log.e(tag, "error while fetching nearby restaurants", error) }
            .onErrorComplete()
    }

    private fun transformResult(restaurantList: List<Restaurant>): Completable {
        return Completable.fromAction {
            restaurantList.forEach { restaurant ->
                Log.d(tag, "Restaurant : $restaurant")
            }
        }
    }

}