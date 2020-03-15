package com.sample.restuarant.search.di.module

import android.content.Context
import com.sample.restuarant.search.di.scope.ApplicationScope
import com.sample.restuarant.search.view.MapViewController
import com.sample.restuarant.search.view.common.MapsUtility
import com.sample.restuarant.search.view.common.PermissionUtils
import dagger.Module
import dagger.Provides

/* @author Dinesh Kumar 
   @creation_date 3/11/2020*/

@Module
class UtilityModule {

    @Provides
    fun providesPermissionUtils(): PermissionUtils {
        return PermissionUtils()
    }

    @Provides
    fun providesMapsUtils(): MapsUtility {
        return MapsUtility()
    }

    @Provides
    @ApplicationScope
    fun providesMapDecorator(mapsUtility: MapsUtility, context: Context): MapViewController {
        return MapViewController(mapsUtility, context)
    }
}