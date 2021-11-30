package com.bahaddindemir.bitcointicker.di

import com.bahaddindemir.bitcointicker.data.repository.account.AccountRepository
import com.bahaddindemir.bitcointicker.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideCheckFirstTimeUseCase(accountRepository: AccountRepository):
            CheckFirstTimeUseCase = CheckFirstTimeUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideCheckLoggedInUserUseCase(accountRepository: AccountRepository):
            CheckLoggedInUserUseCase = CheckLoggedInUserUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideSetFirstTimeUseCase(accountRepository: AccountRepository):
            SetFirstTimeUseCase = SetFirstTimeUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideGeneralUseCases(checkFirstTimeUseCase: CheckFirstTimeUseCase,
                               checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
                               setFirstTimeUseCase: SetFirstTimeUseCase,
                               clearPreferencesUseCase: ClearPreferencesUseCase
    ): GeneralUseCases = GeneralUseCases(checkFirstTimeUseCase, checkLoggedInUserUseCase,
        setFirstTimeUseCase, clearPreferencesUseCase)

    @Provides
    @Singleton
    fun provideClearPreferencesUseCase(accountRepository: AccountRepository):
            ClearPreferencesUseCase = ClearPreferencesUseCase(accountRepository)
}