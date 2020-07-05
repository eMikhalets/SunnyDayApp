package com.emikhalets.sunnydayapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "city_id") val cityId: Int,
    @ColumnInfo(name = "city_name") val cityName: String,
    @ColumnInfo(name = "state_code") val stateCode: String,
    @ColumnInfo(name = "country_code") val countryCode: String,
    @ColumnInfo(name = "country_full") val countryFull: String,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "is_city_added") var isAdded: Boolean
) {
    fun getQuery() = "$cityName, $countryFull"
}