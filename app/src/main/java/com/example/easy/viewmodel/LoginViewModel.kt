package com.example.easy.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.api.AuthService
import com.example.easy.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel(private val context: Context) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val authService = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthService::class.java)

    fun login(email: String, password: String, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authService.login(LoginRequest(email, password))
                
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        // Save token and user info
                        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("token", loginResponse.token)
                            .putInt("user_id", loginResponse.user.id)
                            .putString("user_name", loginResponse.user.name)
                            .putString("user_role", loginResponse.user.role)
                            .apply()
                        
                        _isLoading.value = false
                        onSuccess(true)
                    } else {
                        _error.value = "Respuesta inválida del servidor"
                        onSuccess(false)
                    }
                } else {
                    _error.value = "Credenciales inválidas"
                    onSuccess(false)
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                onSuccess(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}