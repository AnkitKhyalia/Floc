package com.example.floc.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floc.authentication.repository.UserLoginRepository
import com.example.floc.util.Resource
import com.example.floc.util.ValidatorUtils
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val loginRepository: UserLoginRepository

): ViewModel() {
    private var debounceJob: Job? = null
    private val _login = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val login: StateFlow<Resource<FirebaseUser>> = _login
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError
    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError
    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _login.value = Resource.Loading()
            val result = loginRepository.signInWithEmailAndPassword(email, password)
            _login.value = result
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
    fun validatePassword(password: String) {

        viewModelScope.launch {

//            val result = ValidatorUtils.validateAllFields(password = password)
            if (password.isEmpty()) {
                _passwordError.value = "Password cannot be empty"
            }
            else{
                _passwordError.value = null
            }
        }
    }



}
