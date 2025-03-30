package com.example.easy.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy.data.api.BranchService
import com.example.easy.data.model.Branch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BranchViewModel(private val context: Context) : ViewModel() {
    private val _branches = MutableStateFlow<List<Branch>>(emptyList())
    val branches: StateFlow<List<Branch>> = _branches
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val branchService = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BranchService::class.java)

    init {
        fetchBranches()
    }

    private fun fetchBranches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                val token = sharedPrefs.getString("token", "") ?: ""
                
                Log.d("BranchViewModel", "Using token: $token")
                val response = branchService.getBranches("Bearer $token")
                
                Log.d("BranchViewModel", "Response code: ${response.code()}")
                Log.d("BranchViewModel", "Response body: ${response.body()}")
                Log.d("BranchViewModel", "Error body: ${response.errorBody()?.string()}")
                
                if (response.isSuccessful) {
                    _branches.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar las sucursales: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("BranchViewModel", "Error fetching branches", e)
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveBranchId(branchId: Int, businessId: Int) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("selected_branch_id", branchId)
            .putInt("business_id", businessId)
            .apply()
    }
}