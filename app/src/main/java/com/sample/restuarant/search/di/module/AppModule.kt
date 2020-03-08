package com.sample.restuarant.search.di.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

@Module
abstract class AppModule {

    @Binds
    abstract fun providesContext(application: Application): Context


}