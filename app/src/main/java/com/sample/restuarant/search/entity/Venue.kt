package com.sample.restuarant.search.entity

import com.google.gson.annotations.SerializedName

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

data class Venue(
    @SerializedName("venues") val venue: List<Restaurant>
)