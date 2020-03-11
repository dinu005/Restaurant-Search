package com.sample.restuarant.search.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.restuarant.search.controller.RestaurantService
import com.sample.restuarant.search.model.RestaurantModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/* @author Dinesh Kumar 
   @creation_date 3/10/2020*/

class RestaurantViewModelImpl(
    application: Application,
    private val restaurantService: RestaurantService
) : AndroidViewModel(application), RestaurantViewModel {

    private val logTag = "RestaurantViewModelImpl"
    var restaurantLiveDate: MutableLiveData<List<RestaurantModel>> = MutableLiveData()
    var restaurantMap: MutableMap<String, RestaurantModel> = mutableMapOf()

    override fun getRestaurantsObservableData(): LiveData<List<RestaurantModel>> {
        return restaurantLiveDate
    }

    override fun syncRestaurants(latitude: Double, longitude: Double, radius: Int): Completable {
        return restaurantService.getNearByRestaurants(latitude, longitude, radius)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable { restaurants ->
                Completable.fromAction {
                    restaurants.forEach { restaurant ->
                        restaurantMap[restaurant.restaurantId] = restaurant
                    }
                    Log.d(logTag, "Restaurant Map ${restaurantMap.values.toMutableList()}")
                    restaurantLiveDate.value = restaurantMap.values.toMutableList()
                }
            }
    }

}