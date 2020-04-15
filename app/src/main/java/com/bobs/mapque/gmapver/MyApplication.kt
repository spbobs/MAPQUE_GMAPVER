package com.bobs.mapque.gmapver

import com.bobs.baselibrary.base.BaseApplication
import com.bobs.mapque.gmapver.di.dataModule
import com.bobs.mapque.gmapver.di.prefsModule
import com.bobs.mapque.gmapver.di.roomModule
import com.bobs.mapque.gmapver.di.viewModelModule
import com.bobs.mapque.gmapver.util.ADManager
import com.google.android.gms.ads.MobileAds
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(viewModelModule, dataModule, roomModule, prefsModule)
        }

        // debug 일때만 로그 보기
        Logger.addLogAdapter(object: AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return isDebuggable()
            }
        })

        // admob 초기화
        MobileAds.initialize(this) {}

        // 광고 미리 로드
        ADManager.loadAd(this)
    }
}