package org.bumho.ktodo.ui

import androidx.compose.runtime.Composable

@Composable
actual fun ImagePickerButton(
    currentImageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    // iOS image picker not implemented
    // You can add UIImagePickerController here if needed
}