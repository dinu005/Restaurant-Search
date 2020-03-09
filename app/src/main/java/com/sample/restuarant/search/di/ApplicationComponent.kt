package com.sample.restuarant.search.di

import android.app.Application
import com.sample.restuarant.search.di.module.ActivityModule
import com.sample.restuarant.search.di.module.AppModule
import com.sample.restuarant.search.di.module.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

@Component(
    modules = [AndroidInjectionModule::class, AppModule::class, NetworkModule::class, ActivityModule::class]
)
interface ApplicationComponent : AndroidInjector<DaggerApplication> {

    override fun inject(instance: DaggerApplication)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }

}