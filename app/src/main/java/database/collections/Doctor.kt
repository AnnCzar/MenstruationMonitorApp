package database.collections

import android.widget.Spinner
import com.google.firebase.firestore.PropertyName

data class Doctor(
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("login") @set:PropertyName("login") var login: String = "",
    @get:PropertyName("password") @set:PropertyName("password") var password: String = "",
    val role: String,
    val specialisation: String
)
