package com.sample.restuarant.search

import com.sample.restuarant.search.di.DaggerTestApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/* @author Dinesh Kumar 
   @creation_date 3/9/2020*/

class TestApplication : RestaurantApplication() {
    override fun onCreate() {
        injector = DaggerTestApplicationComponent.builder().application(this).create()
        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return injector
    }
}