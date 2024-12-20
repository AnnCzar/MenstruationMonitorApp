package com.example.project

/**
 * Data class representing a Medicine.
 *
 * @property id Unique identifier for the medicine.
 * @property name Name of the medicine.
 * @property isChecked Boolean flag indicating if the medicine has been checked or taken.
 * @property dose The dosage of the medicine.
 * @property time The time the medicine should be taken.
 */
data class Medicine(
    val id: String,
    val name: String,
    var isChecked: Boolean = false,
    val dose: String = "",
    val time: String = ""
)
