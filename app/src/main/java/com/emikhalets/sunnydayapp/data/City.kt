package com.emikhalets.sunnydayapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "last_time") val lastTime: String,
    @ColumnInfo(name = "temperature") val temperature: Int
)