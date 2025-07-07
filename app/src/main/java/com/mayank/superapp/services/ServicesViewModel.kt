package com.mayank.superapp.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServicesViewModel(
    private val repository: ServicesRepository = ServicesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    init { loadServices() }

    fun loadServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val data = repository.fetchServices()
                _uiState.value = ServicesUiState(services = data, loading = false)
            } catch (t: Throwable) {
                _uiState.value = ServicesUiState(error = t.localizedMessage)
            }
        }
    }
}

data class ServicesUiState(
    val services: List<Service> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)
