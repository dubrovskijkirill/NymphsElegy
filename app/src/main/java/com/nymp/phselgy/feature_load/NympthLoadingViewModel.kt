package com.nymp.phselgy.feature_load

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NympthLoadingViewModel @Inject constructor(
    val nympthLoadingRepository: NympthLoadingRepository
): ViewModel()