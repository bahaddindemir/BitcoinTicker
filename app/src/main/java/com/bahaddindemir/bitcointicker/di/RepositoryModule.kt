package com.bahaddindemir.bitcointicker.di

import com.bahaddindemir.bitcointicker.data.local.CoinDao
import com.bahaddindemir.bitcointicker.data.repository.account.AccountRepository
import com.bahaddindemir.bitcointicker.data.repository.account.AccountRepositoryImpl
import com.bahaddindemir.bitcointicker.data.repository.auth.AuthRepository
import com.bahaddindemir.bitcointicker.data.repository.auth.AuthRepositoryImpl
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepositoryImp
import com.bahaddindemir.bitcointicker.data.services.ApiService
import com.bahaddindemir.bitcointicker.data.services.AuthRemoteDataSource
import com.bahaddindemir.bitcointicker.data.services.FireStoreSource
import com.bahaddindemir.bitcointicker.util.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(remoteDataSource: AuthRemoteDataSource):
            AuthRepository = AuthRepositoryImpl(remoteDataSource)

    @Provides
    @Singleton
    fun provideAccountRepository(remoteDataSource: AuthRemoteDataSource,
                                 appPreferences: AppPreferences):
            AccountRepository = AccountRepositoryImpl(remoteDataSource, appPreferences)

    @Provides
    @Singleton
    fun provideCoinRepository(coinDao: CoinDao, fireStore: FireStoreSource,
                              apiService: ApiService, appPreferences: AppPreferences):
            CoinRepository = CoinRepositoryImp(coinDao, fireStore, apiService, appPreferences)
}