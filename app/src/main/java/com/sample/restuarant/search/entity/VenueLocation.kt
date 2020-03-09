package com.sample.restuarant.search.entity

import com.google.gson.annotations.SerializedName

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

data class VenueLocation(
    @SerializedName("address") val address: String,
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lng") val longitude: Double,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("country") val country: String,
    @SerializedName("formattedAddress") val formattedAddress: List<String>
) {
    override fun toString(): String {
        return "VenueLocation(address='$address', latitude=$latitude, longitude=$longitude, city='$city', state='$state', country='$country', formattedAddress='$formattedAddress')"
    }
}