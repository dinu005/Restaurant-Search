package com.sample.restuarant.search.model

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

data class RestaurantModel(
    val restaurantId: String,
    val restaurantName: String,
    val restaurantLocationModel: RestaurantLocationModel,
    val restaurantCategoryModel: RestaurantCategoryModel
)