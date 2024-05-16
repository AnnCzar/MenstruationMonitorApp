package database.collections

import com.google.firebase.firestore.PropertyName
import java.util.*

data class Pregnancy(
    @get:PropertyName("endDatePregnancy") @set:PropertyName("endDatePregnancy") var endDatePregnancy: Date? = null,
    @get:PropertyName("startDatePregnancy") @set:PropertyName("startDatePregnancy") var startDatePregnancy: Date? = null,
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = ""
)
