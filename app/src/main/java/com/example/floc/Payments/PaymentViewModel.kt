package com.example.floc.Payments

import android.app.Activity
import android.content.Context
import android.health.connect.datatypes.units.Length
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.floc.data.Payment
import com.example.floc.data.Plan
import com.example.floc.util.Resource
import com.example.floc.util.UiEvent
import com.example.floc.util.ValidatorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    private val _planStatus = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val planStatus: StateFlow<Resource<Boolean>> = _planStatus
    private val _allPlans = MutableStateFlow<Resource<List<Plan>>>(Resource.Unspecified())
    val allPlans: StateFlow<Resource<List<Plan>>> = _allPlans
    private  val _paymentHistory = MutableStateFlow<Resource<List<Payment>>>(Resource.Unspecified())
    val paymentHistory:StateFlow<Resource<List<Payment>>> = _paymentHistory
    private val _selectedPlan = MutableStateFlow<Plan?>(null)
    val selectedPlan = _selectedPlan.asStateFlow()
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _mobileNumberError = MutableStateFlow<String?>(null)
    val mobileNumberError: StateFlow<String?> = _mobileNumberError
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private var debounceJob: Job? = null
    init {
        checkPlanStatus()
        fetchPlans()
        getPaymentHistory()
    }
    fun savePayment(){
        paymentRepository.savePaymentToFirebase("12345","success")
    }
    fun checkPlanStatus() {
        _planStatus.value = Resource.Loading()
        viewModelScope.launch {
            val result = paymentRepository.checkPlanStatus()
            _planStatus.value =result
        }

    }
    fun handlePlanClick(plan:Plan){
        _selectedPlan.value = plan
    }
    fun getPaymentHistory(){
        _paymentHistory.value = Resource.Loading()
        viewModelScope.launch {
            val result = paymentRepository.getPaymentHistory()
            _paymentHistory.value = result
        }

    }
    fun fetchPlans() {
        _allPlans.value = Resource.Loading()
        viewModelScope.launch {
            val result = paymentRepository.fetchPlans()
            _allPlans.value = result
            Log.d("all plans viewmodel1 ", _allPlans.value.data.toString())
        }



    }
    fun startPayment(context: Context, email: String, contact: String) {

        if(_selectedPlan.value !=null && emailError.value == null && mobileNumberError.value == null  && email.isNotEmpty() && contact.isNotEmpty()){
            paymentRepository.startPayment(context, _selectedPlan.value!!.price, email, contact,_selectedPlan.value!!.planId)
        }
        else{
//            Toast.makeText(context,"Please Select a Plan",Toast.LENGTH_SHORT).show()
            viewModelScope.launch {

            _uiEvent.emit(UiEvent.ShowSnackbar("Select a plan and fill email and mobile no"))
            }

        }

    }
    fun validateEmail( email: String) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
//            delay(delayMillis)
            val result = ValidatorUtils.validateAllFields(email = email)

            _emailError.value = result["email"]
        }

    }
    fun validateContact( contact: String) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
//            delay(delayMillis)
            val result = ValidatorUtils.validateAllFields(mobileNumber = contact)
            _mobileNumberError.value = result["mobileNumber"]
        }
    }
    fun savePayment(transactionId:String, status:String){
        paymentRepository.savePaymentToFirebase(transactionId,status)
    }

}