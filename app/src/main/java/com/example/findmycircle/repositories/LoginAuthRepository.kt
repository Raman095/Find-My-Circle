package com.example.findmycircle.repositories

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.findmycircle.util.LoginNetworkUtil
import com.example.findmycircle.util.dataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoginAuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore
    private val fireBaseAuth = FirebaseAuth.getInstance()

    suspend fun saveLoginState(isLoggedIn: Boolean) {
        dataStore.edit {
            it[booleanPreferencesKey("is_logged_in")] = isLoggedIn
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[booleanPreferencesKey("is_logged_in")] ?: false
    }

    fun isNetworkAvailable(): Boolean {
        return LoginNetworkUtil.isNetworkAvailable(context)
    }

    fun firebaseAuthWithGoogle(
        idToken: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        fireBaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    onResult(true, null)
                }
                else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}