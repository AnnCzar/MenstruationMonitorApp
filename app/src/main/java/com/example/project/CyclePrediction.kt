package com.example.project

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Class responsible for predicting the menstrual cycle and period dates based on user data.
 * It interacts with Firebase Firestore to fetch user cycle data and calculates median cycle lengths and menstruation periods.
 */
class CyclePrediction(private val db: FirebaseFirestore) {

    /**
     * Fetches the user's cycle and menstruation data from Firestore, calculates the median cycle lengths
     * and menstruation periods, and updates the user document with these calculated values.
     *
     * @param userId The ID of the user for whom the cycle data is being calculated.
     * @param onComplete A callback function that is invoked with the calculated median cycle length and period length.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateMedianCycleLength(userId: String, onComplete: (Long) -> Unit) {
        val cycleLengths = mutableListOf<Long>()
        val menstruations = mutableListOf<Long>()

        // sprawdzenie czy dzialania asynchroniczne skonczone
        var operationsCompleted = 0
        val totalOperations = 2

        db.collection("users")
            .document(userId)
            .collection("cycles")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val startDateStr = doc.getString("startDate")
                    val endDateStr = doc.getString("endDate")

                    if (startDateStr != null && endDateStr != null) {
                        try {
                            val startDate: Date = sdf.parse(startDateStr)!!
                            val endDate: Date = sdf.parse(endDateStr)!!

                            val startLocalDate =
                                startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            val endLocalDate =
                                endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                            val menstruationLength =
                                ChronoUnit.DAYS.between(startLocalDate, endLocalDate) + 1
                            menstruations.add(menstruationLength)  // dodaj 1 dzień dla długości menstruacji

                            val cycleLength = doc.getLong("cycleLength")
                            cycleLength?.let {
                                cycleLengths.add(it)
                            }

                        } catch (e: Exception) {
                            Log.e("CyclePrediction", "Error parsing dates: $e")
                        }
                    }
                }
                operationsCompleted++
                // sprawdzenie czy operacje wszystkie sie skonczyly
                if (operationsCompleted == totalOperations) {
                    calculateAndLogMedians(userId, cycleLengths, menstruations, onComplete)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to fetch cycles: $e")
            }

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val cycleLengthMain = userDoc.getLong("cycleLength")?.toInt()
                val menstruationLengthMain = userDoc.getLong("periodLength")?.toInt()

                if (cycleLengthMain != null) {
                    cycleLengths.add(cycleLengthMain.toLong())
                }
                if (menstruationLengthMain != null) {
                    menstruations.add(menstruationLengthMain.toLong())
                }

                operationsCompleted++
                if (operationsCompleted == totalOperations) {
                    calculateAndLogMedians(userId, cycleLengths, menstruations, onComplete)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to fetch user data: $e")
            }
    }

    /**
     * Calculates the median cycle length and menstruation period from the provided lists,
     * updates the user's Firestore document, and logs the results.
     *
     * @param userId The ID of the user whose cycle data is being calculated.
     * @param cycleLengths A list of cycle lengths in days.
     * @param menstruations A list of menstruation lengths in days.
     * @param onComplete A callback function that is invoked with the calculated median cycle length and period length.
     */
    fun calculateAndLogMedians(
        userId: String,
        cycleLengths: MutableList<Long>,
        menstruations: MutableList<Long>,
        onComplete: (Long) -> Unit
    ) {
        val medianCycles = if (cycleLengths.isNotEmpty()) {
            cycleLengths.sort()
            Log.e("CycleLengths", cycleLengths.toString())

            if (cycleLengths.size % 2 == 0) {
                ((cycleLengths[cycleLengths.size / 2] + cycleLengths[cycleLengths.size / 2 - 1]) / 2.0).roundToInt()
                    .toLong()
            } else {
                cycleLengths[cycleLengths.size / 2]
            }
        } else {
            0L // jak nie ma cykli to mediana 0
        }

        val medianMenstruations = if (menstruations.isNotEmpty()) {
            menstruations.sort()
            Log.e("Menstruations", menstruations.toString())

            if (menstruations.size % 2 == 0) {
                ((menstruations[menstruations.size / 2] + menstruations[menstruations.size / 2 - 1]) / 2.0).roundToInt()
                    .toLong()
            } else {
                menstruations[menstruations.size / 2]
            }
        } else {
            0L
        }

        db.collection("users")
            .document(userId)
            .update("periodLength", medianMenstruations, "cycleLength", medianCycles)

        Log.e("MedianCycles", medianCycles.toString())
        Log.e("MedianMenstruations", medianMenstruations.toString())

        onComplete(medianCycles, medianMenstruations)
    }


    private fun onComplete(menstruations: Long, cycleLengths: Long) {

    }


    /**
     * Predicts the next menstruation date based on the user's cycle length and the most recent cycle data.
     *
     * @param userId The ID of the user whose next menstruation date is being predicted.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun predictNextMenstruation(userId: String) {

        var cycleLength: Long
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                cycleLength = userDoc.getLong("cycleLength")!!

                var startDate: String? = null
                db.collection("users").document(userId).collection("cycles")
                    .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val document = querySnapshot.documents[0]
                            startDate = document.getString("startDate")
                        }
                    }
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                if (startDate != null) {

                    val startDate1: Date = startDate?.let { sdf.parse(it) }!!
                    val startLocalDate =
                        startDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val NextMenstruation = startLocalDate.plusDays(cycleLength.toLong())
                    db.collection("users").document(userId).collection("cycles")
                        .orderBy(
                            "startDate",
                            com.google.firebase.firestore.Query.Direction.DESCENDING
                        )
                        .limit(1)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val latestCycle = documents.first()
                                val cycleRef = db.collection("users").document(userId)
                                    .collection("cycles").document(latestCycle.id)
                                cycleRef
                                    .update("nextPeriodDate", NextMenstruation.toString())
                            }
                        }
                }
            }
    }
}

