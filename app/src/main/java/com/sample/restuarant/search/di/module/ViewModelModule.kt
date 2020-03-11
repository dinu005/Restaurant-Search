package com.sample.restuarant.search.di.module

import android.app.Application
import com.sample.restuarant.search.controller.RestaurantService
import com.sample.restuarant.search.di.scope.ApplicationScope
import com.sample.restuarant.search.viewmodel.RestaurantViewModel
import com.sample.restuarant.search.viewmodel.RestaurantViewModelImpl
import dagger.Module
import dagger.Provides

/* @author Dinesh Kumar 
   @creation_date 3/10/2020*/

@Module
class ViewModelModule {

    @Provides
    @ApplicationScope
    fun providesRestaurantViewModel(
        application: Application,
        restaurantService: RestaurantService
    ): RestaurantViewModel {
        return RestaurantViewModelImpl(application, restaurantService)
    }
}