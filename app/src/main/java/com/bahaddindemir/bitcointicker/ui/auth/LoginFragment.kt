package com.bahaddindemir.bitcointicker.ui.auth

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.databinding.FragmentLoginBinding
import com.bahaddindemir.bitcointicker.extension.*
import com.bahaddindemir.bitcointicker.data.model.AuthFieldsValidation
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.ui.home.HomeActivity
import com.bahaddindemir.bitcointicker.data.model.Resource
import com.bahaddindemir.bitcointicker.util.showSoftInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: AuthViewModel by viewModels()

    override
    fun getLayoutId() = R.layout.fragment_login

    override
    fun setBindingVariables() {
        binding.authViewModel = viewModel
    }

    override
    fun setupObservers() {
        viewModel.toSignupButton.observe(this) {
            openSignUp()
        }

        viewModel.successResponse.observe(this) {
            hideLoading()
            openHome()
        }

        viewModel.failResponse.observe(this) {
            hideLoading()
            // ToDo: There is a bug about snackBar
            //requireView().showSnackBar(resources.getString(R.string.some_error))
        }

        viewModel.validationException.observe(this) {
            when (it) {
                AuthFieldsValidation.EMPTY_EMAIL.value -> {
                    binding.etEmail.requestFocus()
                    showSoftInput(binding.etEmail, requireContext())
                    requireView().showSnackBar(resources.getString(R.string.empty_email))
                }
                AuthFieldsValidation.INVALID_EMAIL.value -> {
                    binding.etEmail.requestFocus()
                    showSoftInput(binding.etEmail, requireContext())
                    requireView().showSnackBar(resources.getString(R.string.invalid_email))
                }
                AuthFieldsValidation.EMPTY_PASSWORD.value -> {
                    binding.etPassword.requestFocus()
                    showSoftInput(binding.etPassword, requireContext())
                    requireView().showSnackBar(resources.getString(R.string.empty_password))
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.authResponse.collect {
                when (it) {
                    Resource.Loading -> {
                        hideKeyboard()
                        showLoading()
                    }
                }
            }
        }
    }

    private fun openSignUp() {
        navigateSafe(LoginFragmentDirections.actionOpenSignUpFragment())
    }

    private fun openHome() {
        requireActivity().openActivityAndClearStack(HomeActivity::class.java)
    }
}