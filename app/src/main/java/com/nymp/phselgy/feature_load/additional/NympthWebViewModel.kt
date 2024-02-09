package com.nymp.phselgy.feature_load.additional

import android.net.Uri
import android.webkit.ValueCallback
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nymp.phselgy.feature_load.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NympthWebViewModel @Inject constructor(
    val preferencesManager: PreferencesManager
) : ViewModel() {
    var filePathCallback: ValueCallback<Array<Uri>>? = null
    var capturedImageUri: Uri? = null
    var isDialogVisible  by mutableStateOf(false)

    var progress by mutableStateOf(0f)

    lateinit var onBackHandler: () -> Unit

}