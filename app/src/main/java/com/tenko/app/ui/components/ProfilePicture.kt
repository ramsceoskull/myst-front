package com.tenko.app.ui.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tenko.app.R
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.Tekhelet

@Composable
fun ProfilePicture(route: Uri?, size: Dp = 150.dp, isBorderVisible: Boolean = true, isChatMessage: Boolean = false) {
    AsyncImage(
        model = route,
        contentDescription = "Profile Photo",
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(
                width = if(isBorderVisible) 4.dp else if(isChatMessage) 2.dp else 0.dp,
                color = if(isChatMessage) AntiFlashWhite else Tekhelet,
                shape = CircleShape),
        placeholder = painterResource(R.drawable.profile_picture_placeholder),
        error = painterResource(R.drawable.profile_picture_placeholder),
        contentScale = ContentScale.Crop
    )
}