package com.bahaddindemir.bitcointicker.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bahaddindemir.bitcointicker.R

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val splashColor = colorResource(id = R.color.splash)
    val splashAccentColor = colorResource(id = R.color.splash_accent)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(splashColor, splashAccentColor)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(bottom = 50.dp),
            text = stringResource(id = R.string.splash_text),
            color = Color.White,
            fontSize = 100.sp
        )
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 50.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}
