package com.example.uptowncampus

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

/**
 * An [Application] subclass that initializes the Koin dependency injection framework and sets up the
 * [appModule] module.
 * This class is responsible for bootstrapping the application and providing a global context for its components.
 */

class UptownCampusApplication : Application() {
    /**
     * Called when the application is starting up. This method initializes Koin by starting the
     * global context and setting the application context, logging level, and modules.
     */
    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@UptownCampusApplication)
            modules(appModule)
        }
    }
}