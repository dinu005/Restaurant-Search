package com.sample.restuarant.search.di.module

import com.sample.restuarant.search.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/


@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}