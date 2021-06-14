package com.dev.`in`.drogon.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Utils {
    private val Context.datastore by preferencesDataStore(name = "Drogon")

    @Singleton
    @Provides
    fun providesDatastore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.datastore
}