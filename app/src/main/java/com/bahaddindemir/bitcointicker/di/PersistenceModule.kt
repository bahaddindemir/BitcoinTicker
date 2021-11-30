package com.bahaddindemir.bitcointicker.di

import android.app.Application
import androidx.room.Room
import com.bahaddindemir.bitcointicker.data.local.AppDatabase
import com.bahaddindemir.bitcointicker.data.local.CoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase {
        val dbName = "BitcoinTicker.db"
        return Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
            dbName
        )
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinDao(database: AppDatabase): CoinDao {
        return database.coinDao()
    }
}