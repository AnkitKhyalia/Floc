package com.example.floc.data

import com.google.firebase.Timestamp

data class Payment (

    val userId:String,
    val paymentID:String,
    val status:String,
    val amount:Double,
    val planId:String,
    val paymentDate: Timestamp,

){
    constructor() : this("", "", "", 0.0,"" ,Timestamp.now())
}