package com.android.cell.mate

import android.app.Application
import timber.log.Timber

/**
 * Created by kombo on 2019-09-11.
 */
class CellMate: Application() {

    override fun onCreate() {
        super.onCreate()

        //init logger
        Timber.uprootAll()
        Timber.plant(Timber.DebugTree())
    }
}