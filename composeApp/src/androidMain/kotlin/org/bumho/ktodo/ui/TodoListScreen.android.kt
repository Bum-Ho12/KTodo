package org.bumho.ktodo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest

@Composable
actual fun TodoImageThumbnail(imagePath: String?) {
    imagePath?.let {
        AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .build()
            ,
            contentDescription = "Todo image",
            modifier = Modifier
                .size(60.dp)
                .padding(start = 8.dp),
            contentScale = ContentScale.Crop
        )
    }
}