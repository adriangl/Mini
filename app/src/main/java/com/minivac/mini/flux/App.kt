package com.minivac.mini.flux

import android.app.Application
import com.minivac.mini.BuildConfig
import com.minivac.mini.dagger.*
import com.minivac.mini.log.DebugTree
import com.minivac.mini.log.Grove
import com.minivac.mini.misc.collectDeviceBuildInformation
import com.squareup.leakcanary.LeakCanary
import kotlin.properties.Delegates

private var _app: App by Delegates.notNull<App>()
val app: App get() = _app

class App :
        Application(),
        ComponentManager by DefaultComponentManager() {

    val exceptionHandlers: MutableList<Thread.UncaughtExceptionHandler> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        _app = this
        if (BuildConfig.DEBUG) {
            Grove.plant(DebugTree(true))
            Grove.d { collectDeviceBuildInformation(this) }
        }

        registerComponent(object : ComponentFactory<AppComponent> {
            override fun createComponent(): AppComponent {
                return DaggerAppComponent.builder()
                        .appModule(AppModule(app))
                        .build()
            }

            override val componentType = AppComponent::class
        })

        val appComponent = findComponent(AppComponent::class)
        val stores = appComponent.stores()
        initStores(stores.values.toList())

        registerSystemCallbacks(appComponent.dispatcher(), this)

        val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        exceptionHandlers.add(exceptionHandler)
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            exceptionHandlers.forEach { it.uncaughtException(thread, error) }
        }

        configureLeakCanary()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        trimComponents(level)
    }


    private fun configureLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)
    }

}

