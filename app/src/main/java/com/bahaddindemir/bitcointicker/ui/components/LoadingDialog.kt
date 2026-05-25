package com.bahaddindemir.bitcointicker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bahaddindemir.bitcointicker.ui.theme.BitcoinTickerColors

@Composable
fun LoadingDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    hint: String? = null
) {
    if (!isVisible) return
    val colors = BitcoinTickerColors.current

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = colors.background
            )
            if (!hint.isNullOrEmpty()) {
                Text(
                    text = hint,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoadingDialogPreview() {
    LoadingDialog(isVisible = true, hint = "Loading")
}
