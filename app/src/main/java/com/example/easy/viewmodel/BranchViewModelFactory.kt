package com.example.easy.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BranchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BranchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BranchViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}