package com.bahaddindemir.bitcointicker.ui.auth

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.AuthFieldsValidation
import com.bahaddindemir.bitcointicker.data.model.Resource
import com.bahaddindemir.bitcointicker.databinding.FragmentLoginBinding
import com.bahaddindemir.bitcointicker.extension.hideKeyboard
import com.bahaddindemir.bitcointicker.extension.navigateSafe
import com.bahaddindemir.bitcointicker.extension.openActivityAndClearStack
import com.bahaddindemir.bitcointicker.extension.showSnackBar
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.ui.main.MainActivity
import com.bahaddindemir.bitcointicker.util.showSoftInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            if (it) {
                hideLoading()
                openHome()
            } else {
                hideLoading()
                //ToDo: There is a bug about snackBar
                //requireView().showSnackBar(resources.getString(R.string.some_error))
            }
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

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
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
    }

    private fun openSignUp() {
        navigateSafe(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    private fun openHome() {
        requireActivity().openActivityAndClearStack(MainActivity::class.java)
    }
}