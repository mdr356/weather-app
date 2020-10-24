package com.trinity.weatherapp.dagger.component

import com.trinity.weatherapp.dagger.ActivityProvider
import com.trinity.weatherapp.dagger.module.ViewModelModule
import com.trinity.weatherapp.dagger.module.AppModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.DaggerApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    ActivityProvider::class, ViewModelModule::class, AppModule::class])
interface AppComponent : AndroidInjector<DaggerApplication>