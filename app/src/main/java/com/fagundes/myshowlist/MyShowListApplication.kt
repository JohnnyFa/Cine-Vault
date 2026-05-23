package com.fagundes.myshowlist

import android.app.Application
import com.fagundes.myshowlist.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyShowListApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyShowListApplication)
            modules(
                appModule,
            )
        }
    }
}
