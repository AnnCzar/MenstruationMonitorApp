package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class PregnancyBegginingActivity : AppCompatActivity() {
    private lateinit var pregnancy_beg_text : TextView
    private lateinit var  insert_date_pregnancy_start : TextInputEditText
    private lateinit var accept_pregnancy_beg_button : Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pregnancy_beggining)

        pregnancy_beg_text = findViewById(R.id.pregnancy_beg_text)
        insert_date_pregnancy_start = findViewById(R.id.insert_date_pregnancy_start)
        accept_pregnancy_beg_button = findViewById(R.id.accept_pregnancy_beg_button)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        accept_pregnancy_beg_button.setOnClickListener {
            openMainWindowPregnancyActivity(userId)
        }
    }

    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }


}
