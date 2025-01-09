package com.example.floc.data

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val remainingClicks:Int=10

){
    constructor() : this("", "", "", "")
}
