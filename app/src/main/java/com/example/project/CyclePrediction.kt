package com.example.project

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import kotlin.math.floor

class CyclePrediction(private val db: FirebaseFirestore) {

    // Funkcja pobiera dane z ostatniego roku i oblicza medianę długości cyklu
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateMedianCycleLength(userId: String, onComplete: (Double?) -> Unit) {
        val currentDate = LocalDate.now()  // Pobiera aktualną datę
        val yearAgo = currentDate.minusYears(1)  // Ustawia datę sprzed roku
        val cycles = mutableListOf<Int>()  // Lista do przechowywania długości cykli

        // Pobiera dane cykli z bazy Firestore
        db.collection("users")
            .document(userId)
            .collection("cycles")
            .whereGreaterThan("startDate", yearAgo)  // Filtruje dane z ostatniego roku
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val startDate = doc.getDate("startDate")
//                    val endDate - doc.
                    val cycleLength = doc.getLong("cycleLength")?.toInt()  // Pobiera długość cyklu
                    if (cycleLength != null) {
                        cycles.add(cycleLength)  // Dodaje do listy, jeśli wartość jest niepusta
                    }
                }

                // Jeśli mamy dane o cyklach, obliczamy medianę
                if (cycles.isNotEmpty()) {
                    cycles.sort()
                    val median = if (cycles.size % 2 == 0) {
                        // Jeśli liczba elementów jest parzysta, mediana to średnia z dwóch środkowych wartości
                        (cycles[cycles.size / 2] + cycles[cycles.size / 2 - 1]) / 2.0
                    } else {
                        // Jeśli liczba elementów jest nieparzysta, mediana to wartość środkowa
                        cycles[cycles.size / 2].toDouble()
                    }
                    onComplete(median)
                } else {
                    onComplete(null)  // Brak danych do obliczenia mediany
                }
            }
            .addOnFailureListener {
                onComplete(null)  // Obsługa błędu w przypadku nieudanego pobierania danych
            }
    }

    // Funkcja przewiduje następny cykl na podstawie mediany lub długości wprowadzonej przez użytkownika
    @RequiresApi(Build.VERSION_CODES.O)
    fun predictNextCycle(userId: String, userEnteredLength: Int, onComplete: (String) -> Unit) {
        calculateMedianCycleLength(userId) { median ->
            val predictedLength = median ?: userEnteredLength.toDouble()  // Jeśli brak danych, używamy długości podanej przez użytkownika
            onComplete("Przewidywana długość cyklu: $predictedLength dni")
        }
    }

    // Funkcja zapisuje dane nowego cyklu do bazy po zakończeniu menstruacji
    fun updateCycleData(userId: String, cycleLength: Int, menstruationLength: Int, startDate: LocalDate) {
        val cycleData = hashMapOf(
            "cycleLength" to cycleLength,
            "menstruationLength" to menstruationLength,
            "startDate" to startDate
        )

        db.collection("users")
            .document(userId)
            .collection("cycles")
            .add(cycleData)
            .addOnSuccessListener {
                println("Dane cyklu zostały zapisane pomyślnie!")
            }
            .addOnFailureListener { e ->
                println("Błąd podczas zapisywania danych cyklu: $e")
            }
    }
}
