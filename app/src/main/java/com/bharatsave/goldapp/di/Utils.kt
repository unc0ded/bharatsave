package com.bharatsave.goldapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.bharatsave.goldapp.data.db.UserDatabase
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

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): UserDatabase {
        return synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java,
                "user_database"
            ).build()
        }
    }

    @Singleton
    @Provides
    fun provideUserDao(userDatabase: UserDatabase) = userDatabase.userDao()
}