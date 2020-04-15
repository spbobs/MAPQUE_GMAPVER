package com.bobs.mapque.gmapver.di

import androidx.room.Room
import com.bobs.mapque.gmapver.map.vm.MapViewModel
import com.bobs.mapque.gmapver.searchlist.data.room.AppDatabase
import com.bobs.mapque.gmapver.searchlist.data.source.SearchListDataSource
import com.bobs.mapque.gmapver.searchlist.data.source.SearchListRepository
import com.bobs.mapque.gmapver.searchlist.vm.SearchListViewModel
import com.bobs.mapque.gmapver.util.GpsTracker
import com.bobs.mapque.gmapver.util.ext.sharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MapViewModel(get(), get()) }
    viewModel { SearchListViewModel(get()) }
}

val dataModule = module {
    single { GpsTracker(androidContext()) }
    single<SearchListDataSource> { SearchListRepository(get())}
}

val RD = "ROOM_DATABASE"

// 룸 di
val roomModule = module {
    single(named(RD)) {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java, "search_list_db"
        ).build()
    }

    single { (get(named(RD)) as AppDatabase).getSearchItemDao() }
}

val prefsModule = module {
    single { androidApplication().sharedPreferences()}
}