package com.emikhalets.sunnydayapp.di

import com.emikhalets.sunnydayapp.data.api.ApiFactory
import com.emikhalets.sunnydayapp.data.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideApiService(): ApiService = ApiFactory.get()
}