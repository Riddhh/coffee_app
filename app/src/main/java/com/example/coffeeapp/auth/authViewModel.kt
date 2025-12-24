package com.example.coffeeapp.auth

import android.R.attr.name
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.micro.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val loading: Boolean = false,
    val isAuthed: Boolean = false,
    val error: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = AuthState(loading = true)

                val res = NetworkClient.authApi.login(
                    LoginReq(email, password)
                )

                // ✅ SAVE JWT HERE
                TokenStore.save(getApplication(), res.token)

                _state.value = AuthState(isAuthed = true)

            } catch (e: Exception) {
                _state.value = AuthState(error = e.message)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = AuthState(loading = true)

                NetworkClient.authApi.register(
                    RegisterReq(
                        name = name.trim(),
                        email = email.trim(),
                        password = password
                    )
                )

                _state.value = AuthState(isAuthed = true)

            } catch (e: Exception) {
                _state.value = AuthState(error = e.message)
            }
        }
    }
}
