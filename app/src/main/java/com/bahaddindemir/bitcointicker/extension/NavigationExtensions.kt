package com.bahaddindemir.bitcointicker.extension

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions

fun NavController.navigateToBottomDestination(destinationId: Int) {
    runCatching {
        navigate(
            resId = destinationId,
            args = null,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
            }
        )
    }
}
