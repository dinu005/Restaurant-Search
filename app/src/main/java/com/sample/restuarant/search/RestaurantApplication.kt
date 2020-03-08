package com.sample.restuarant.search

import com.sample.restuarant.search.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

class RestaurantApplication : DaggerApplication() {

    lateinit var injector: AndroidInjector<out DaggerApplication>

    override fun onCreate() {
        injector = DaggerApplicationComponent.factory().create(this)
        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return injector
    }
}