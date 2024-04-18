package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class PregnancyBegginingActivity : AppCompatActivity() {
    private lateinit var pregnancy_beg_text : TextView
    private lateinit var  insert_date_pregnancy_start : TextInputEditText
    private lateinit var accept_pregnancy_beg_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pregnancy_beggining)

        pregnancy_beg_text = findViewById(R.id.pregnancy_beg_text)
        insert_date_pregnancy_start = findViewById(R.id.insert_date_pregnancy_start)
        accept_pregnancy_beg_button = findViewById(R.id.accept_pregnancy_beg_button)

        accept_pregnancy_beg_button.setOnClickListener {
            openMainWindowPregnancyActivity()
        }
    }

    private fun openMainWindowPregnancyActivity() {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        startActivity(intent)
    }


}
