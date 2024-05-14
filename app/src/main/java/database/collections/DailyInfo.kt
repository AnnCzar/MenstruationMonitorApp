package database.collections

import com.google.firebase.firestore.PropertyName

data class DailyInfo(
    @get:PropertyName("bodyTemperature") @set:PropertyName("bodyTemperature") var bodyTemperature: Double = 0.0,
    @get:PropertyName("medicationsTaken") @set:PropertyName("medicationsTaken") var medicationsTaken: Map<String, Boolean> = mapOf(),
    @get:PropertyName("mood") @set:PropertyName("mood") var mood: String = "",
    @get:PropertyName("symptoms") @set:PropertyName("symptoms") var symptoms: Map<String, Boolean> = mapOf(),
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = "",
    @get:PropertyName("waterConsumption") @set:PropertyName("waterConsumption") var waterConsumption: Double = 0.0,
    @get:PropertyName("weight") @set:PropertyName("weight") var weight: Double = 0.0
)

