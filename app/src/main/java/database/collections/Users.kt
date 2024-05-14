package database.collections
import com.google.firebase.firestore.PropertyName
import java.util.*

data class Users(
    @get:PropertyName("cycleLength") @set:PropertyName("cycleLength") var cycleLength: Int = 0,
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("lastPeriodDate") @set:PropertyName("lastPeriodDate") var lastPeriodDate: Date? = null,
    @get:PropertyName("login") @set:PropertyName("login") var login: String = "",
    @get:PropertyName("periodLength") @set:PropertyName("periodLength") var periodLength: Int = 0,
    @get:PropertyName("statusPregnancy") @set:PropertyName("statusPregnancy") var statusPregnancy: Boolean = false,
    @get:PropertyName("weight") @set:PropertyName("weight") var weight: Double = 0.0
)