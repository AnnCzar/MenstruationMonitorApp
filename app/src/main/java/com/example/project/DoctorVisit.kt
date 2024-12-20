package com.example.project

/**
 * A data class representing a doctor's visit.
 * It contains information about the visit, including the doctor's name,
 * visit date, time, whether the visit has been checked, any extra information,
 * and the address of the doctor.
 *
 * @param id The unique identifier for the doctor's visit (mutable to allow updates).
 * @param doctorName The name of the doctor for the visit.
 * @param visitDate The date of the visit in the format "yyyy-MM-dd".
 * @param time The time of the visit (e.g., "14:00").
 * @param isChecked Boolean flag indicating whether the visit has been checked (e.g., confirmed or marked).
 * @param extraInfo Any additional information related to the visit.
 * @param address The address of the doctor's practice or office.
 */
data class DoctorVisit(
    var id: String,
    val doctorName: String,
    val visitDate: String,
    val time: String,
    var isChecked: Boolean,
    val extraInfo: String,
    val address: String
)