package com.sample.restuarant.search.model

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

data class RestaurantCategoryModel(
    val categoryId: String,
    val categoryName: String
) {
    override fun toString(): String {
        return "RestaurantCategoryModel(categoryId='$categoryId', categoryName='$categoryName')"
    }
}