package com.example.easy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.api.AuthService
import com.example.easy.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val authService = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthService::class.java)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState.Loading
                Log.d("LoginViewModel", "Attempting login with email: $email")
                
                val response = authService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        Log.d("LoginViewModel", "Login successful: ${loginResponse.user.name}")
                        _uiState.value = LoginUiState.Success
                    }
                } else {
                    Log.e("LoginViewModel", "Login failed: ${response.errorBody()?.string()}")
                    _uiState.value = LoginUiState.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error", e)
                _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}