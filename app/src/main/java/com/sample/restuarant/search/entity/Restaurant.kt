package com.sample.restuarant.search.entity

import com.google.gson.annotations.SerializedName

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

data class Restaurant(
    @SerializedName("id") val restaurantId: String,
    @SerializedName("name") val restaurantName: String,
    @SerializedName("location") val restaurantLocation: VenueLocation,
    @SerializedName("categories") val restaurantCategories: List<VenueCategory>
) {
    override fun toString(): String {
        return "Restaurant(restaurantId='$restaurantId', restaurantName='$restaurantName', restaurantLocation=$restaurantLocation, restaurantCategories=$restaurantCategories)"
    }
}