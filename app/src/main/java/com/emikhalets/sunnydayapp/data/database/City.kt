package com.emikhalets.sunnydayapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
    val state: String,
    val country: String,
    val lon: Double,
    val lat: Double,
    var isSearched: Boolean = false
)