package com.example.cermatitestapp

import android.app.Application
import com.example.cermatitestapp.modules.githubApiModule
import com.example.cermatitestapp.modules.mainViewModel
import com.example.cermatitestapp.modules.userSearchResultRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {

    companion object {
        /*
         * Singleton pattern for Application.
         * to refer to this object, use:
         * MyApp.getInstance()
         *
         * in alternative, you could also use:
         * (application as MyApp) or (getApplication() as MyApp)
         */
        private lateinit var mInstance: MyApp

        // Getter for singleton MyApp
        @Synchronized
        fun getInstance(): MyApp {
            return mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(listOf(
                githubApiModule,
                userSearchResultRepository,
                mainViewModel
            ))
        }
    }
}