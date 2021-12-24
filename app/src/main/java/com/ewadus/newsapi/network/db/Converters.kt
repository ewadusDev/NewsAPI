package com.ewadus.newsapi.network.db

import androidx.room.TypeConverter
import com.ewadus.newsapi.network.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }

}





