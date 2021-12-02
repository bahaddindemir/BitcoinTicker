package com.bahaddindemir.bitcointicker.ui.mycoin

import android.view.View
import androidx.fragment.app.viewModels
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinImage
import com.bahaddindemir.bitcointicker.data.model.coin.CoinLocalization
import com.bahaddindemir.bitcointicker.databinding.FragmentMyCoinBinding
import com.bahaddindemir.bitcointicker.ui.adapter.MyCoinAdapter
import com.bahaddindemir.bitcointicker.ui.auth.AuthViewModel
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.ui.home.HomeViewModel
import com.bahaddindemir.bitcointicker.ui.viewholder.MyCoinViewHolder
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class MyCoinFragment : BaseFragment<FragmentMyCoinBinding>(), MyCoinViewHolder.Delegate {
    private val viewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val myCoinAdapter = MyCoinAdapter(this)

    override fun getLayoutId() = R.layout.fragment_my_coin

    override fun setBindingVariables() {
        binding.viewModel = viewModel
        binding.myCoinsRecyclerview.adapter = myCoinAdapter
    }

    override fun setupObservers() {
        val firebaseUser = authViewModel.user
        firebaseUser?.let {
            subscribeGetMyCoinFavoriteList(firebaseUser)
        }
    }

    private val firebaseStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val disposables = CompositeDisposable()

    private fun subscribeGetMyCoinFavoriteList(firebaseUser: FirebaseUser) {
        val disposable = getMyCoinFavoriteList(firebaseUser)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                it.message?.run {
                }
            })
        disposables.add(disposable)
    }

    private fun getMyCoinFavoriteList(firebaseUser: FirebaseUser) = Completable.create { emitter ->
        val documentRefFavorite = firebaseStore.collection(myCoinCollectionName).document(firebaseUser.uid)
        val collectionMyFavorite = documentRefFavorite.collection(myFavoriteList)
        if (!emitter.isDisposed) {
            val noteListener =
                collectionMyFavorite.addSnapshotListener { value, error ->
                    if (error != null) {
                        // Do stuff
                    }

                    if (!value?.documents.isNullOrEmpty()) {
                        val coinDetailItemList = ArrayList<CoinDetailItem>()
                        value?.let {
                            for (documentSnapshot in value.documents) {
                                val dataHashMap =
                                    documentSnapshot.data?.get(coinDetailItem) as HashMap<*, *>?
                                val image = dataHashMap?.get(coinImage) as HashMap<*, *>?
                                val id: String = (dataHashMap?.get(coinId) ?: "id") as String
                                val symbol = dataHashMap?.get(coinSymbol) as String?
                                val name = dataHashMap?.get(coinName) as String?
                                val hashingAlgorithm = dataHashMap?.get("hashingAlgorithm") as String?
                                val favorite = dataHashMap?.get(coinFavorite) as Boolean?
                                val description = dataHashMap?.get(coinDescription) as HashMap<*, *>?
                                val marketData = dataHashMap?.get(coinImage) as HashMap<*, *>?
                                val descriptionTr = description?.get("tr") as String?
                                val smallImageUrl = image?.get("small")
                                val thumb = image?.get("thumb") as String?
                                val large = image?.get("large") as String?
                                val small = image?.get("small") as String?
                                val imageModel =
                                    CoinImage(thumb ?: "", small ?: "", large ?: "")
                                val descriptionModel = CoinLocalization(
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    tr = descriptionTr ?: "",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                                )
                                val coinDetailItem = CoinDetailItem(
                                    id,
                                    symbol,
                                    name,
                                    imageModel,
                                    null,
                                    hashingAlgorithm,
                                    descriptionModel,
                                    favorite
                                )
                                coinDetailItemList.add(coinDetailItem)
                            }
                            myCoinAdapter.setData(coinDetailItemList)
                            binding.myCoinsRecyclerview.adapter = myCoinAdapter
                        }
                    }
                }
        }
    }

    override fun onItemClick(coinDetailItem: CoinDetailItem, view: View) {

    }

    companion object {
        const val myCoinCollectionName = "MyCoins"
        const val coinId = "id"
        const val coinSymbol = "symbol"
        const val coinName = "name"
        const val coinImage = "image"
        const val coinDescription = "description"
        const val coinFavorite = "favorite"
        const val myFavoriteList = "MyFavoriteList"
        const val coinDetailItem = "coinDetailItem"
    }
}