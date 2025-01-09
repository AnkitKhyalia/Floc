package com.example.floc.di

import com.example.floc.Payments.PaymentRepository
import com.example.floc.authentication.repository.UserLoginRepository
import com.example.floc.authentication.repository.UserRegisterRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton

    fun provideFirebaseAuth()= FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase()= Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseStorage()= Firebase.storage

    @Singleton
    @Provides
    fun provideUserRepository(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore): UserRegisterRepository {
        return UserRegisterRepository(firebaseAuth, firestore)
    }
    @Singleton
    @Provides

    fun provideUserLoginRepository(firebaseAuth: FirebaseAuth,firestore: FirebaseFirestore): UserLoginRepository {
        return UserLoginRepository(firebaseAuth,firestore)
    }
    @Singleton
    @Provides

    fun providePaymentRepository(firebaseAuth: FirebaseAuth,firestore: FirebaseFirestore): PaymentRepository {
        return PaymentRepository(firebaseAuth,firestore)
    }

}