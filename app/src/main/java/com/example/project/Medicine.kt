package com.example.project

data class Medicine(
    val id: String,
    val name: String,
    var isChecked: Boolean = false,
    val dose: String = "",
    val time: String = ""
)
