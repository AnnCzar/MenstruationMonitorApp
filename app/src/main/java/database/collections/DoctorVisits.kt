package database.collections

import com.google.firebase.firestore.PropertyName
import java.util.*

data class DoctorVisits(
    @get:PropertyName("date") @set:PropertyName("date") var date: Date? = null,
    @get:PropertyName("nameOfDoctor") @set:PropertyName("nameOfDoctor") var nameOfDoctor: String = "",
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = ""
)
