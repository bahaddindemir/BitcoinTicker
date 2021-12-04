package com.bahaddindemir.bitcointicker.data.services

import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreSource @Inject constructor() {
    private val firebaseStore = FirebaseFirestore.getInstance()

    fun addCoinToFavorite(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) =
        Completable.create { emitter -> val documentFavorite =
            firebaseStore.collection(coinCollectionName).document(firebaseUser.uid)

            val collectionMyFavorite = documentFavorite.collection(favoriteList)

            val saveDataParam = HashMap<String, Any>()
            saveDataParam[coinDetail] = coinDetailItem
            if (!emitter.isDisposed) {
                collectionMyFavorite.document()
                    .set(saveDataParam)
                    .addOnSuccessListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener {
                        emitter.onError(it)
                    }
            }
        }

    fun deleteFavoriteCoin(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) =
        Completable.create { emitter -> val documentFavorite =
            firebaseStore.collection(coinCollectionName).document(firebaseUser.uid)

            val collectionMyFavorite = documentFavorite.collection(favoriteList)

            if (!emitter.isDisposed) {
                collectionMyFavorite.document()
                    .delete()
                    .addOnSuccessListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener {
                        emitter.onError(it)
                    }
            }
        }

    companion object {
        const val coinCollectionName = "FavoriteCoins"
        const val favoriteList = "favoriteList"
        const val coinDetail = "coinDetail"
    }
}