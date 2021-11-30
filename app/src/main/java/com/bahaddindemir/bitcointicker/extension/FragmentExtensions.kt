package com.bahaddindemir.bitcointicker.extension

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bahaddindemir.bitcointicker.util.hideSoftInput

fun Fragment.hideKeyboard() = hideSoftInput(requireActivity())

fun Fragment.showError(message: String, retryActionName: String? = null, action: (() -> Unit)? = null) =
    requireView().showSnackBar(message, retryActionName, action)

fun <A : Activity> Fragment.openActivityAndClearStack(activity: Class<A>) {
    requireActivity().openActivityAndClearStack(activity)
}

fun <A : Activity> Fragment.openActivity(activity: Class<A>) {
    requireActivity().openActivity(activity)
}

fun <T> Fragment.getNavigationResultLiveData(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.removeNavigationResultObserver(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.remove<T>(key)

fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun Fragment.onBackPressedCustomAction(action: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override
            fun handleOnBackPressed() {
                action()
            }
        }
    )
}

fun Fragment.navigateSafe(directions: NavDirections, navOptions: NavOptions? = null) {
    findNavController().navigate(directions, navOptions)
}

fun Fragment.backToPreviousScreen() {
    findNavController().navigateUp()
}