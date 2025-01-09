package com.example.floc.data

data class Plan (
    val planId: String,
    val name: String,
    val durationInMonths: Int,
    val price: Double

){
    constructor() : this("", "", 6, 0.0)
}