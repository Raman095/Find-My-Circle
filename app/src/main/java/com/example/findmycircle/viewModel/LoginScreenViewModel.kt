package com.example.findmycircle.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmycircle.model.LoginState
import com.example.findmycircle.repositories.LoginAuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authRepository: LoginAuthRepository
): ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val loggedIn = authRepository.isUserLoggedIn()
            state = state.copy(isLoggedIn = loggedIn)
        }
    }

    fun onGoogleSignInClicked(): Boolean {
        if (!authRepository.isNetworkAvailable()) {
            state = state.copy(error = "No Internet Connection")
            return false
        }

        state = state.copy(isLoading = true, error = null)
        return true
    }

    fun handleFirebaseAuth(idToken: String) {
        authRepository.firebaseAuthWithGoogle(idToken) { success, error ->

            if(success) {
                viewModelScope.launch {
                    authRepository.saveLoginState(true)
                    state = state.copy(isLoading = false, isLoggedIn = true)
                }
            }
            else {
                state = state.copy(isLoading = false, error = error)
            }
        }
    }

    fun onSignInFailure(message: String) {
        state = state.copy(isLoading = false, error = message)
    }

    fun logout() {
        viewModelScope.launch {
        FirebaseAuth.getInstance().signOut()   // 🔥 important
        authRepository.saveLoginState(false)
        state = state.copy(isLoggedIn = false)
    }
    }

}