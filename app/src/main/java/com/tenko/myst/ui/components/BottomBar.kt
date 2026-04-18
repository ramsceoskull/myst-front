package com.tenko.myst.ui.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tenko.myst.ui.theme.White

@Composable
fun BottomBar(onSend: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp
                ),
                ambientColor = Color.Black,
                spotColor = Color.Black,
                clip = false
            ),
            containerColor = White,
        ) {
            ChatInput(
                onSend = onSend,
                modifier = Modifier.imePadding().padding(16.dp)
            )
        }
    }
}