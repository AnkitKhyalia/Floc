package com.example.floc.authentication.repository

import com.example.floc.data.User
import com.example.floc.util.Constants.User_Collection
import com.example.floc.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRegisterRepository @Inject constructor(
    private  val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register


    suspend fun createAccountWithEmailAndPassword(user: User, password: String) {

            _register.value = Resource.Loading()
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
                authResult.user?.let { saveUserInfo(it.uid, user) }
            } catch (e: Exception) {
                _register.value = Resource.Error(e.message.toString())
            }

    }

    private suspend fun saveUserInfo(userUid: String, user: User) {
        try {
            firestore.collection(User_Collection)
                .document(userUid)
                .set(user)
                .await()
            _register.value = Resource.Success(user)
        } catch (e: Exception) {
            _register.value = Resource.Error(e.message.toString())
        }
    }

}