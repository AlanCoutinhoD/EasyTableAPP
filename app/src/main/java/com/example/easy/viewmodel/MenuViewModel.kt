package com.example.easy.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.api.MenuService
import com.example.easy.data.model.MenuResponse
import com.example.easy.data.model.Branch  // Add this import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow  // Add this import
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuViewModel(private val context: Context) : ViewModel() {
    private val _selectedBranchId = MutableStateFlow<Int?>(null)
    val selectedBranchId: StateFlow<Int?> = _selectedBranchId

    init {
        // Get the selected branch ID from SharedPreferences
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        _selectedBranchId.value = prefs.getInt("selected_branch_id", -1).takeIf { it != -1 }
    }
    private val _menu = MutableStateFlow<List<MenuResponse>>(emptyList())
    val menu: StateFlow<List<MenuResponse>> = _menu

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedBranch = MutableStateFlow<Branch?>(null)
    val selectedBranch = _selectedBranch.asStateFlow()

    fun setSelectedBranch(branch: Branch) {
        _selectedBranch.value = branch
    }

    private val menuService = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MenuService::class.java)

    fun fetchMenu() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token", "") ?: ""
                val businessId = prefs.getInt("business_id", -1)

                if (businessId == -1) {
                    _error.value = "No se ha seleccionado un negocio"
                    return@launch
                }

                val response = menuService.getBusinessMenu(businessId, "Bearer $token")
                if (response.isSuccessful) {
                    _menu.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar el menú"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}