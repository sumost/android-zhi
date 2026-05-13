package com.androidzhi.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 小智AI Android应用
 * 
 * 基于 py-xiaozhi 移植的Android版本
 * 实现了WebSocket/MQTT双协议通信、语音交互等功能
 */
@HiltAndroidApp
class AndroidZhiApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: AndroidZhiApplication
            private set
    }
}