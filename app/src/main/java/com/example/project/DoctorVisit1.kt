package com.example.project

/**
 * A simplified data class representing a doctor's visit.
 * This class contains the essential information about the visit, including the doctor's name,
 * visit date, and a unique identifier for the visit.
 *
 * @param id The unique identifier for the doctor's visit (default is an empty string).
 * @param doctorName The name of the doctor for the visit (default is an empty string).
 * @param visitDate The date of the visit in the format "yyyy-MM-dd" (default is an empty string).
 */

data class DoctorVisit1(
    var id: String = "",
    val doctorName: String = "",
    val visitDate: String = ""
)