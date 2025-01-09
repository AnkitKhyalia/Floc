package com.example.floc.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floc.authentication.repository.UserLoginRepository
import com.example.floc.util.ValidatorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val loginRepository: UserLoginRepository

): ViewModel()
{   private var debounceJob: Job? = null
    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState = _forgotPasswordState.asStateFlow()
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = ForgotPasswordState.Loading
            val result = loginRepository.sendPasswordResetEmail(email)
            _forgotPasswordState.value = when {
                result.isSuccess -> ForgotPasswordState.Success
                result.isFailure -> ForgotPasswordState.Error(result.exceptionOrNull()?.message ?: "Unknown error occurred")
                else -> ForgotPasswordState.Error("Unexpected error")
            }
        }
    }
    fun validateEmail( email: String, delayMillis: Long = 1000L) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(delayMillis)
            val result = ValidatorUtils.validateAllFields(email = email)

            _emailError.value = result["email"]
        }

    }

}
sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}