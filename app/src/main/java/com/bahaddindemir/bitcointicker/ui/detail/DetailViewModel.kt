package com.bahaddindemir.bitcointicker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import com.bahaddindemir.bitcointicker.util.AppPreferences
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val coinItem = MutableSharedFlow<String>(replay = 1)
    private var refreshJob: Job? = null

    private val _events = MutableSharedFlow<DetailUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private val _uiState = MutableStateFlow(
        DetailUiState(defaultCurrency = defaultCurrency)
    )
    val uiState = _uiState.asStateFlow()

    private val coinDetailState: StateFlow<CoinResource<CoinDetailItem>> = coinItem
        .flatMapLatest { coinItemId -> coinRepository.loadCoinDetail(coinItemId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoinResource.Loading
        )

    val defaultCurrency: String
        get() = appPreferences.defaultCurrency ?: "BTC"

    init {
        observeCoinDetail()
    }

    fun startRefreshing(coinItemId: String) {
        if (_uiState.value.coinId == coinItemId && refreshJob?.isActive == true) return

        refreshJob?.cancel()
        _uiState.update { state ->
            state.copy(
                coinId = coinItemId,
                defaultCurrency = defaultCurrency
            )
        }
        refreshJob = viewModelScope.launch {
            while (true) {
                coinItem.emit(coinItemId)
                delay(_uiState.value.refreshIntervalMillis)
            }
        }
    }

    fun onIntervalChange(value: String) {
        _uiState.update { state -> state.copy(intervalText = value) }
    }

    fun onConfirmIntervalClick() {
        val refreshInterval = _uiState.value.intervalText.trim().toLongOrNull()
            ?.takeIf { interval -> interval > 0 }
            ?: return

        _uiState.update { state ->
            state.copy(refreshIntervalMillis = refreshInterval)
        }
        _uiState.value.coinId?.let { coinId -> startRefreshing(coinId) }
    }

    fun onFavoriteClick(firebaseUser: FirebaseUser?) {
        val coinDetailItem = _uiState.value.coinDetailItem ?: return
        val user = firebaseUser ?: return

        if (_uiState.value.isFavorite) {
            deleteFavorite(user, coinDetailItem)
        } else {
            addFavorite(user, coinDetailItem)
        }
    }

    private fun observeCoinDetail() {
        viewModelScope.launch {
            coinDetailState.collect { resource ->
                when (resource) {
                    CoinResource.Loading -> {
                        _uiState.update { state -> state.copy(isLoading = true) }
                    }

                    is CoinResource.Success -> {
                        _uiState.update { state ->
                            val detail = resource.data
                            state.copy(
                                title = detail?.name.orEmpty(),
                                coinDetailItem = detail,
                                isFavorite = detail?.isFavorite ?: state.isFavorite,
                                lastUpdatedDate = if (detail != null) getCurrentDate() else state.lastUpdatedDate,
                                isLoading = false
                            )
                        }
                    }

                    is CoinResource.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false
                            )
                        }
                        _events.emit(DetailUiEvent.DetailLoadFailed)
                    }
                }
            }
        }
    }

    private fun addFavorite(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        viewModelScope.launch {
            try {
                coinRepository.addFavoriteCoin(firebaseUser, coinDetailItem)
                updateFavoriteStateAfterSuccess()
            } catch (_: Exception) {
                _events.emit(DetailUiEvent.FavoriteChangeFailed)
            }
        }
    }

    private fun deleteFavorite(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        viewModelScope.launch {
            try {
                coinRepository.deleteFavoriteCoin(firebaseUser, coinDetailItem)
                updateFavoriteStateAfterSuccess()
            } catch (_: Exception) {
                _events.emit(DetailUiEvent.FavoriteChangeFailed)
            }
        }
    }

    private fun updateFavoriteStateAfterSuccess() {
        _uiState.update { state ->
            val isFavorite = !state.isFavorite
            state.coinDetailItem?.let { coinDetailItem ->
                coinDetailItem.isFavorite = isFavorite
                coinRepository.updateFavoriteCoin(coinDetailItem)
            }
            state.copy(isFavorite = isFavorite)
        }
    }

    private fun getCurrentDate(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}
