package com.bahaddindemir.bitcointicker.ui.auth

import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.databinding.ActivityAuthBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>() {
    override
    fun getLayoutId() = R.layout.activity_auth
}