package database.collections
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalDate

data class Users(
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("login") @set:PropertyName("login") var login: String = "",
    @get:PropertyName("password") @set:PropertyName("password") var password: String = "",
    @get:PropertyName("cycleLength") @set:PropertyName("cycleLength") var cycleLength: Int = 0,
    @get:PropertyName("lastPeriodDate") @set:PropertyName("lastPeriodDate") var lastPeriodDate: LocalDate? = null,
    @get:PropertyName("periodLength") @set:PropertyName("periodLength") var periodLength: Int = 0,
    @get:PropertyName("statusPregnancy") @set:PropertyName("statusPregnancy") var statusPregnancy: Boolean = false,
    @get:PropertyName("weight") @set:PropertyName("weight") var weight: Double = 0.0,
    val role: String
)