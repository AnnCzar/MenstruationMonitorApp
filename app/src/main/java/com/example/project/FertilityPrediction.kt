import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import kotlin.math.floor

class FertilityPrediction(private val db: FirebaseFirestore) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun predictOvulationAndFertileDays(
        userId: String,
        temperature: Double?,
        mucusType: String?,
        onComplete: (String) -> Unit
    ) {
        // Pobieramy dane z ostatnich kilku dni, aby określić wzorce temperatury i śluzu
        val currentDate = LocalDate.now()

        db.collection("users")
            .document(userId)
            .collection("dailyInfo")
            .get()
            .addOnSuccessListener { documents ->
                val tempList = mutableListOf<Double>()
                var fertileDays = mutableListOf<LocalDate>()
                var ovulationDay: LocalDate? = null

                for (doc in documents) {
                    val date = doc.getDate("date")?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate()
                    val temp = doc.getDouble("temperature")
                    val mucus = doc.getString("mucusType")

                    if (temp != null && date != null) {
                        tempList.add(temp)

                        // Logika przewidywania owulacji i dni płodnych
                        if (mucus == "rozciągliwy" || mucus == "przejrzysty") {
                            fertileDays.add(date)
                        }
                    }
                }

                // Jeśli mamy wystarczającą liczbę danych, analizujemy
                if (tempList.size >= 5) {
                    val latestTemps = tempList.takeLast(5)  // Ostatnie 5 dni temperatur
                    val tempIncrease = latestTemps.maxOrNull() ?: 0.0

                    if (temperature != null && temperature > tempIncrease) {
                        ovulationDay = currentDate
                        fertileDays.add(currentDate.minusDays(1))
                        fertileDays.add(currentDate.minusDays(2))
                        fertileDays.add(currentDate.minusDays(3))
                    }
                }

                saveFertileDaysAndOvulation(userId, fertileDays, ovulationDay)

                onComplete("Dni płodne: ${fertileDays.joinToString(", ")}, Przewidywany dzień owulacji: $ovulationDay")
            }
            .addOnFailureListener {
                onComplete("Błąd w pobieraniu danych.")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveFertileDaysAndOvulation(
        userId: String,
        fertileDays: List<LocalDate>,
        ovulationDay: LocalDate?
    ) {
        val data = hashMapOf(
            "fertileDays" to fertileDays.map { it.toString() },
            "ovulationDay" to ovulationDay?.toString()
        )

        db.collection("users")
            .document(userId)
            .collection("fertilityPredictions")
            .document(LocalDate.now().toString())
            .set(data)
            .addOnSuccessListener {
                println("Przewidywane dni płodne i owulacja zostały zapisane pomyślnie!")
            }
            .addOnFailureListener { e ->
                println("Błąd podczas zapisywania przewidywań: $e")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveDailyInfo(userId: String, temperature: Double?, mucusType: String?) {
        val data = hashMapOf(
            "temperature" to temperature,
            "mucusType" to mucusType,
            "date" to LocalDate.now().toString()
        )

        db.collection("users")
            .document(userId)
            .collection("dailyInfo")
            .document(LocalDate.now().toString())
            .set(data)
            .addOnSuccessListener {
                println("Dzienny wpis został zapisany pomyślnie!")
            }
            .addOnFailureListener { e ->
                println("Błąd podczas zapisywania dziennego wpisu: $e")
            }
    }
}
