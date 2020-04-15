package com.bobs.mapque.gmapver.searchlist.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bobs.mapque.gmapver.searchlist.data.model.SearchItem

@Database(entities = arrayOf(SearchItem::class), version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun getSearchItemDao(): SearchItemDao
}