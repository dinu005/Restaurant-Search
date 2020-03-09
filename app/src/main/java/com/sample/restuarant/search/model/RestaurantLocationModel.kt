package com.sample.restuarant.search.model

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

data class RestaurantLocationModel(
    val restaurantAddress: String,
    val latitude: Double,
    val longitude: Double
) {
    override fun toString(): String {
        return "RestaurantLocationModel(restaurantAddress='$restaurantAddress', latitude=$latitude, longitude=$longitude)"
    }
}