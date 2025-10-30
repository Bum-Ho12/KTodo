package org.bumho.ktodo.ui

import androidx.compose.runtime.Composable

@Composable
actual fun ImagePickerButton(
    currentImageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    // Desktop image picker not implemented
    // You can add JFileChooser here if needed
}