package com.tenko.myst.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.White

@Composable
fun PhotoActionsRow(
    imageUrl: String?,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Profile Photo",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        ActionChip(
            text = "Editar foto",
            onClick = onEditClick,
            backgroundColor = PompAndPower,
            contentColor = White
        )

        ActionChip(
            text = "Eliminar foto",
            onClick = onRemoveClick,
            backgroundColor = SweetGrey,
            contentColor = White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPhotoActionsRow() {
    PhotoActionsRow(
        imageUrl = null,
        onEditClick = {},
        onRemoveClick = {}
    )
}