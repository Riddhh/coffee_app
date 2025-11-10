package com.example.coffeeapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val loading: Boolean = false,
    val isAuthed: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = AuthState(loading = true)
                val res = ApiClient.login(email, password)
                if (res.token != null) {
                    // you can save res.token in DataStore later
                    _state.value = AuthState(isAuthed = true)
                } else {
                    _state.value = AuthState(error = res.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _state.value = AuthState(error = e.message)
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = AuthState(loading = true)
                val res = ApiClient.register(email, password)
                if (res.token != null) {
                    _state.value = AuthState(isAuthed = true)
                } else {
                    _state.value = AuthState(error = res.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                _state.value = AuthState(error = e.message)
            }
        }
    }
}
