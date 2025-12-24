package com.example.coffeeapp.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.auth.AuthApi
import com.example.coffeeapp.auth.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PersonalInfoUiState(
    val loading: Boolean = true,
    val profile: UserProfile? = null,
    val error: String? = null
)

class PersonalInfoViewModel(
    private val api: AuthApi
) : ViewModel() {

    private val _state = MutableStateFlow(PersonalInfoUiState())
    val state: StateFlow<PersonalInfoUiState> = _state

    fun load() {
        viewModelScope.launch {
            try {
                val me = api.me()
                _state.value = PersonalInfoUiState(
                    loading = false,
                    profile = me
                )
            } catch (e: Exception) {
                _state.value = PersonalInfoUiState(
                    loading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }
}
