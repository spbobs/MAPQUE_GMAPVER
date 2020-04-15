package com.bobs.mapque.gmapver.searchlist.data.room

import androidx.room.TypeConverter
import org.joda.time.DateTime

class DateConverter {
    @TypeConverter
    fun longToDateTime(time: Long?) = DateTime(time)

    @TypeConverter
    fun datetimeToLong(dateTime: DateTime) = dateTime.millis
}