package com.bahaddindemir.bitcointicker.util

import com.bahaddindemir.bitcointicker.data.repository.account.AccountRepository
import javax.inject.Inject

class GeneralUseCases(
    val checkFirstTimeUseCase: CheckFirstTimeUseCase,
    val checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
    val setFirstTimeUseCase: SetFirstTimeUseCase,
    val clearPreferencesUseCase: ClearPreferencesUseCase
)

class CheckFirstTimeUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    operator fun invoke() = accountRepository.isFirstTime()
}

class CheckLoggedInUserUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    operator fun invoke() = accountRepository.isLoggedIn()
}

class SetFirstTimeUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    operator fun invoke(isFirstTime: Boolean) = accountRepository.setFirstTime(isFirstTime)
}

class ClearPreferencesUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    operator fun invoke() = accountRepository.clearPreferences()
}