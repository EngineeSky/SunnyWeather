package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication : Application() {

    companion object {  // 伴生对象
        const val TOKEN = "Xu0FqYHAyriwgakf"
        @SuppressLint("StaticFieldLeak")    // 让AS忽略内存泄漏风险.p580-581
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}