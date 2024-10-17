//package com.example.project
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import com.google.firebase.firestore.FirebaseFirestore
//import java.time.LocalDate
//
//class FertilityPrediction(private val db: FirebaseFirestore) {
//
//    // Funkcja przewiduje owulację na podstawie danych o temperaturze i śluzie
//    fun predictOvulation(userId: String, onComplete: (String) -> Unit) {
//        db.collection("users")
//            .document(userId)
//            .collection("fertilityData")
//            .get()
//            .addOnSuccessListener { documents ->
//                var temperatureRise = false
//                var fertileMucus = false
//
//                for (doc in documents) {
//                    val temperature = doc.getDouble("temperature")
//                    val mucusType = doc.getString("mucusType")
//
//                    // Sprawdza, czy występuje wzrost temperatury powyżej normy
//                    if (temperature != null && temperature > 37.0) {
//                        temperatureRise = true
//                    }
//
//                    // Sprawdza, czy śluz jest płodny (przezroczysty i rozciągliwy)
//                    if (mucusType == "clear_stretchy") {
//                        fertileMucus = true
//                    }
//                }
//
//                if (temperatureRise && fertileMucus) {
//                    onComplete("Wykryto dni płodne. Prawdopodobna owulacja.")
//                } else {
//                    onComplete("Brak oznak płodności.")
//                }
//            }
//            .addOnFailureListener { e ->
//                onComplete("Błąd przy przewidywaniu owulacji: $e")
//            }
//    }
//
//    // Funkcja zapisuje codzienne dane o temperaturze i śluzie
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun saveFertilityData(userId: String, temperature: Double, mucusType: String) {
//        val fertilityData = hashMapOf(
//            "temperature" to temperature,
//            "mucusType" to mucusType,
//            "date" to LocalDate.now()
//        )
//
//        db.collection("users")
//            .document(userId)
//            .collection("fertilityData")
//            .add(fertilityData)
//            .addOnSuccessListener {
//                println("Dane o płodności zostały zapisane pomyślnie!")
//            }
//            .addOnFailureListener { e ->
//                println("Błąd przy zapisywaniu danych o płodności: $e")
//            }
//    }
//}
