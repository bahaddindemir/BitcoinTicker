package com.bahaddindemir.bitcointicker.data.services

import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreSource @Inject constructor() {
    private val firebaseStore = FirebaseFirestore.getInstance()

    suspend fun addCoinToFavorite(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        val documentFavorite = firebaseStore.collection(coinCollectionName)
            .document(firebaseUser.uid)

        val collectionMyFavorite = documentFavorite.collection(favoriteList)

        val saveDataParam = HashMap<String, Any>()
        saveDataParam[coinDetail] = coinDetailItem
        collectionMyFavorite.document()
            .set(saveDataParam)
            .await()
    }

    suspend fun deleteFavoriteCoin(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        val documentFavorite = firebaseStore.collection(coinCollectionName)
            .document(firebaseUser.uid)

        val collectionMyFavorite = documentFavorite.collection(favoriteList)

        collectionMyFavorite.document()
            .delete()
            .await()
    }

    companion object {
        const val coinCollectionName = "FavoriteCoins"
        const val favoriteList = "favoriteList"
        const val coinDetail = "coinDetail"
    }
}
