package com.fagundes.myshowlist.feat.options.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearUserDataUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val auth: FirebaseAuth,
    private val clearUserDataUseCase: ClearUserDataUseCase,
) : ViewModel() {
    val currentUser: FirebaseUser? = auth.currentUser

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            clearUserDataUseCase()
            auth.signOut()
            onComplete()
        }
    }
}
