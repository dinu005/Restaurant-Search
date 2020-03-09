package com.sample.restuarant.search.entity

import com.google.gson.annotations.SerializedName

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

data class VenueCategory(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("primary") val isPrimary: Boolean
) {
    override fun toString(): String {
        return "VenueCategory(id='$id', name='$name', isPrimary=$isPrimary)"
    }
}