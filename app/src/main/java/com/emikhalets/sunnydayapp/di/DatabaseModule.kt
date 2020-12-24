package com.emikhalets.sunnydayapp.di

import android.content.Context
import com.emikhalets.sunnydayapp.data.database.AppDatabase
import com.emikhalets.sunnydayapp.data.database.CitiesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.get(context)

    @Singleton
    @Provides
    fun providesCitiesDao(database: AppDatabase): CitiesDao = database.citiesDao()
}