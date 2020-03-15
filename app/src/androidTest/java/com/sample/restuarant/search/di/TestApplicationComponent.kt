package com.sample.restuarant.search.di

import android.app.Application
import com.sample.restuarant.search.TestApplication
import com.sample.restuarant.search.controller.RestaurantServiceImplTest
import com.sample.restuarant.search.di.module.AppModule
import com.sample.restuarant.search.di.scope.ApplicationScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Component(
    modules = [AndroidSupportInjectionModule::class, MockNetworkModule::class, AppModule::class]
)
@ApplicationScope
interface TestApplicationComponent : AndroidInjector<TestApplication> {

    fun inject(test: RestaurantServiceImplTest)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun networkModule(networkModule: MockNetworkModule): Builder

        fun create(): TestApplicationComponent
    }

}