package com.sample.restuarant.search.mock

import com.sample.restuarant.search.entity.*

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

class MockEntities {

    fun getRestaurantEntities(): RestaurantResponse {

        val restaurantList = mutableListOf<Restaurant>()

        val venueCategory = VenueCategory("id1", "restaurant", true)

        val venueLocationOne = VenueLocation(
            "Somewhere in globe",
            1.0,
            1.0,
            "city 1",
            "state 1",
            "country 1",
            listOf("Somewhere in globe", "city 1", "state 1")
        )
        val restaurantOne =
            Restaurant("1", "Restaurant One", venueLocationOne, listOf(venueCategory))

        val venueLocationTwo = VenueLocation(
            "Somewhere in globe",
            1.0,
            -1.0,
            "city 2",
            "state 1",
            "country 1",
            listOf("Somewhere in globe", "city 2", "state 1")
        )
        val restaurantTwo =
            Restaurant("1", "Restaurant Two", venueLocationTwo, listOf(venueCategory))

        restaurantList.add(restaurantOne)
        restaurantList.add(restaurantTwo)

        return RestaurantResponse(Venue(restaurantList))
    }

    fun getRestaurantEntitiesWithEmptyCategory(): RestaurantResponse {

        val restaurantList = mutableListOf<Restaurant>()

        val venueLocationOne = VenueLocation(
            "Somewhere in globe",
            1.0,
            1.0,
            "city 1",
            "state 1",
            "country 1",
            listOf("Somewhere in globe", "city 1", "state 1")
        )
        val restaurantOne =
            Restaurant("1", "Restaurant One", venueLocationOne, listOf())

        restaurantList.add(restaurantOne)

        return RestaurantResponse(Venue(restaurantList))
    }

    fun getRestaurantEntitiesWithEmptyAddress(): RestaurantResponse {

        val restaurantList = mutableListOf<Restaurant>()
        val venueCategory = VenueCategory("id1", "restaurant", true)
        val venueLocationOne = VenueLocation(
            "Somewhere in globe",
            1.0,
            1.0,
            "city 1",
            "state 1",
            "country 1",
            listOf()
        )
        val restaurantOne =
            Restaurant("1", "Restaurant One", venueLocationOne, listOf(venueCategory))

        restaurantList.add(restaurantOne)

        return RestaurantResponse(Venue(restaurantList))
    }

    fun getEmptyRestaurantListResponse(): RestaurantResponse {
        return RestaurantResponse(Venue(emptyList()))
    }

}