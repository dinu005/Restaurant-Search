package com.sample.restuarant.search.controller

import android.util.Log
import com.sample.restuarant.search.entity.Restaurant
import com.sample.restuarant.search.entity.VenueCategory
import com.sample.restuarant.search.entity.VenueLocation
import com.sample.restuarant.search.model.RestaurantCategoryModel
import com.sample.restuarant.search.model.RestaurantLocationModel
import com.sample.restuarant.search.model.RestaurantModel
import com.sample.restuarant.search.network.RestaurantApi
import io.reactivex.Single

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

class RestaurantServiceImpl(private val restaurantApi: RestaurantApi) : RestaurantService {

    private val tag = "RestaurantServiceImpl"

    override fun getNearByRestaurants(): Single<List<RestaurantModel>> {
        return restaurantApi.syncNearByRestaurants(
            "40.74224,-73.99386", 250,
            "browse", "20200301"
        )
            .flatMap { restaurantList -> transformResult(restaurantList.response.venue) }
            .doOnError { error -> Log.e(tag, "error while fetching nearby restaurants", error) }
            .onErrorReturn { emptyList() }
    }

    private fun transformResult(restaurantList: List<Restaurant>): Single<List<RestaurantModel>> {
        return Single.fromCallable {
            val restaurantModelList = mutableListOf<RestaurantModel>()
            restaurantList.forEach { restaurant ->
                val restaurantLocation = transformLocationData(restaurant.restaurantLocation)
                val restaurantCategory = transformCategories(restaurant.restaurantCategories)

                val restaurantModel = RestaurantModel(
                    restaurant.restaurantId, restaurant.restaurantName,
                    restaurantLocation, restaurantCategory
                )
                restaurantModelList.add(restaurantModel)
            }
            restaurantModelList
        }
    }

    private fun transformLocationData(venueLocation: VenueLocation): RestaurantLocationModel {
        var locationAddress = ""
        venueLocation.formattedAddress.forEach { address ->
            locationAddress += address
        }

        if (locationAddress.isEmpty()) {
            locationAddress = venueLocation.address
        }

        return RestaurantLocationModel(
            locationAddress, venueLocation.latitude, venueLocation.longitude
        )
    }

    private fun transformCategories(venueCategories: List<VenueCategory>): RestaurantCategoryModel {
        var categoryId = ""
        var categoryName = ""
        venueCategories.forEach { category ->
            if (category.isPrimary) {
                categoryId = category.id
                categoryName = category.name
            }
        }
        return RestaurantCategoryModel(categoryId, categoryName)
    }

}