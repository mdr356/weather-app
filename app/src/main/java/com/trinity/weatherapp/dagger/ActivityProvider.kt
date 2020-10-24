package com.trinity.weatherapp.dagger

import com.trinity.weatherapp.FirstFragment
import com.trinity.weatherapp.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityProvider {
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun firstFragment(): FirstFragment

}