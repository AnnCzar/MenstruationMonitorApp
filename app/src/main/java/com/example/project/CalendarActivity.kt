package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CalendarActivity : AppCompatActivity(){

    private lateinit var daysToEndOfMenstruaction: EditText
    private lateinit var button_reg: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        daysToEndOfMenstruaction = findViewById(R.id.daysToEndOfMenstruaction)
//        button_reg = findViewById(R.id.button_register)
    }


}
