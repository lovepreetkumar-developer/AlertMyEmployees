package com.alert.me

import android.app.Application
import com.alert.me.data.preferences.PreferenceProvider
import com.alert.me.utils.CustomProgress
import com.alert.me.utils.FieldsValidator
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import timber.log.Timber
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class MainApplication : Application(), KodeinAware {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override val kodein = Kodein.lazy {

        import(androidXModule(this@MainApplication))

        bind() from singleton { FieldsValidator() }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { CustomProgress() }
    }
}