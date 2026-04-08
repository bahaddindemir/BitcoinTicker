package com.bahaddindemir.bitcointicker.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bahaddindemir.bitcointicker.util.hideLoadingDialog
import com.bahaddindemir.bitcointicker.util.showLoadingDialog

abstract class BaseFragment<VB : ViewBinding>(private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB) :
    Fragment() {
    private var _binding: VB? = null
    open val binding get() = _binding!!
    private var progressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFragmentArguments()
        setUpViews()
        observeAPICall()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        registerListeners()
    }

    override fun onPause() {
        unRegisterListeners()
        super.onPause()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    open fun registerListeners() {}

    open fun unRegisterListeners() {}

    open fun getFragmentArguments() {}

    open fun setUpViews() {}

    open fun observeAPICall() {}

    open fun setupObservers() {}

    fun showLoading() {
        hideLoading()
        progressDialog = showLoadingDialog(requireActivity(), null)
    }

    fun showLoading(hint: String?) {
        hideLoading()
        progressDialog = showLoadingDialog(requireActivity(), hint)
    }

    fun hideLoading() = hideLoadingDialog(progressDialog, requireActivity())
}