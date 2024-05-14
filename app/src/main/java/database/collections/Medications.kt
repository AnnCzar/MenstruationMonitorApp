package database.collections
import com.google.firebase.firestore.PropertyName

data class Medications(
    @get:PropertyName("dosage") @set:PropertyName("dosage") var dosage: String? = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("time") @set:PropertyName("time") var time: String = "",
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = ""
)