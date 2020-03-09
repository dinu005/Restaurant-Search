package com.sample.restuarant.search.mock

import com.google.gson.JsonArray
import com.google.gson.JsonObject

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

class MockApiResponse {

    fun successResponse(): String {
        val metaObject = JsonObject()
        metaObject.addProperty("code", 200)
        metaObject.addProperty("requestId", "5ac51d7e6a607143d811cecb")

        val locationObject = JsonObject()
        locationObject.addProperty("address", "180 Orchard St")
        locationObject.addProperty("crossStreet", "btwn Houston & Stanton St")
        locationObject.addProperty("lat", 40.72173744277209)
        locationObject.addProperty("lng", -73.98800687282996)
        locationObject.addProperty("distance", 8)
        locationObject.addProperty("postalCode", "10002")
        locationObject.addProperty("cc", "US")
        locationObject.addProperty("city", "New York")
        locationObject.addProperty("state", "NY")
        locationObject.addProperty("country", "United States")

        val formattedAddress = JsonArray()
        formattedAddress.add("180 Orchard St (btwn Houston & Stanton St)")
        formattedAddress.add("New York, NY 10002")
        formattedAddress.add("United States")

        val categoryObject = JsonObject()
        categoryObject.addProperty("id", "4bf58dd8d48988d1d5941735")
        categoryObject.addProperty("name", "Hotel Bar")
        categoryObject.addProperty("pluralName", "Hotel Bars")
        categoryObject.addProperty("shortName", "Hotel Bar")

        val iconObject = JsonObject()
        iconObject.addProperty("prefix", "https://ss3.4sqi.net/img/categories_v2/travel/hotel_bar_")
        iconObject.addProperty("suffix", ".png")

        categoryObject.add("icon", iconObject)
        categoryObject.addProperty("primary", true)

        val categoryArray = JsonArray()
        categoryArray.add(categoryObject)

        locationObject.add("formattedAddress", formattedAddress)

        val venueObject = JsonObject()
        venueObject.addProperty("id", "5642aef9498e51025cf4a7a5")
        venueObject.addProperty("name", "Mr. Purple")
        venueObject.add("location", locationObject)
        venueObject.add("categories", categoryArray)

        val venueArray = JsonArray()
        venueArray.add(venueObject)

        val responseObject = JsonObject()
        responseObject.add("venues", venueArray)

        val finalResponse = JsonObject()
        finalResponse.add("meta", metaObject)
        finalResponse.add("response", responseObject)

        return finalResponse.toString()
    }

}