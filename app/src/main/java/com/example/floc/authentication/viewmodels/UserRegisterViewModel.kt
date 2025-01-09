package com.example.floc.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floc.authentication.repository.UserRegisterRepository
import com.example.floc.data.User
import com.example.floc.util.Resource
import com.example.floc.util.ValidatorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRegisterViewModel @Inject constructor(
    private  val registerRepository: UserRegisterRepository

): ViewModel() {
    val register : Flow<Resource<User>> = registerRepository.register
    private var debounceJob: Job? = null

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _mobileNumberError = MutableStateFlow<String?>(null)
    val mobileNumberError: StateFlow<String?> = _mobileNumberError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        viewModelScope.launch {
            registerRepository.createAccountWithEmailAndPassword(user, password)
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
    fun validateMobile( contact: String,delayMillis: Long = 1000L) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(delayMillis)
            val result = ValidatorUtils.validateAllFields(mobileNumber = contact)
            _mobileNumberError.value = result["mobileNumber"]
        }
    }
    fun validatePassword(password: String, delayMillis: Long = 1000L) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(delayMillis)
            val result = ValidatorUtils.validateAllFields(password = password)
            _passwordError.value = result["password"]
        }
    }
    fun validateName(name: String, delayMillis: Long = 1000L){
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(delayMillis)
            val result = ValidatorUtils.validateAllFields(name = name)
            _nameError.value = result["name"]

        }
    }

}