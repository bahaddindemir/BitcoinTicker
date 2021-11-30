package com.bahaddindemir.bitcointicker.ui.intro

import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.databinding.ActivityIntroBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    override fun getLayoutId() = R.layout.activity_intro
}