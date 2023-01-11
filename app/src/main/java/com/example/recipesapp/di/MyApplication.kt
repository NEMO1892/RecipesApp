package com.example.recipesapp.di

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    private fun initAppComponent() {
        appComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this, this))
            .build()
    }

    companion object {
        lateinit var appComponent: ApplicationComponent
    }
}