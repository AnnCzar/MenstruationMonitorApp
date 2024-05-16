package database.collections

import com.google.firebase.firestore.PropertyName
import java.util.*

data class Cycles(
    @get:PropertyName("endDate") @set:PropertyName("endDate") var endDate: Date? = null,
    @get:PropertyName("predictedNextPeriod") @set:PropertyName("predictedNextPeriod") var predictedNextPeriod: Date? = null,
    @get:PropertyName("predictedOvulation") @set:PropertyName("predictedOvulation") var predictedOvulation: Date? = null,
    @get:PropertyName("startDate") @set:PropertyName("startDate") var startDate: Date? = null,
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = ""
)