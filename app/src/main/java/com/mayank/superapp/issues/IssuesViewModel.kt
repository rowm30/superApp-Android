package com.mayank.superapp.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** ViewModel exposing UI state for the issues screen */
class IssuesViewModel(
    private val repository: IssuesRepository = IssuesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<IssuesUiState>(IssuesUiState())
    val uiState: StateFlow<IssuesUiState> = _uiState.asStateFlow()

    init {
        loadIssues()
    }

    fun loadIssues() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val issues = repository.fetchIssues()
                _uiState.value = IssuesUiState(issues = issues, loading = false)
            } catch (t: Throwable) {
                _uiState.value = IssuesUiState(error = t.localizedMessage)
            }
        }
    }
}

data class IssuesUiState(
    val issues: List<Issue> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)
