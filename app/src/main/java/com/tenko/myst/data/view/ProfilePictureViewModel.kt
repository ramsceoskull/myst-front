package com.tenko.myst.data.view

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfilePictureViewModel : ViewModel() {
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri : StateFlow<Uri?> = _photoUri

    fun updatePhoto(uri: Uri?) {
        _photoUri.value = uri
    }

    fun removePhoto() {
        _photoUri.value = null
    }
}