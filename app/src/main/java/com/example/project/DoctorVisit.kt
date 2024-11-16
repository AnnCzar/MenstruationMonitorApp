package com.example.project

data class DoctorVisit(
    var id: String,
    val doctorName: String,
    val visitDate: String,
    val time: String,
    var isChecked: Boolean,
    val extraInfo: String,
    val address: String
)