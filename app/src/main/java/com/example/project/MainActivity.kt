package com.example.project

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
//    private lateinit var userInput: EditText
    private lateinit var button_log: Button
//    private lateinit var textView: TextView
//    private lateinit var nextViewButton: Button
//    private var numTimesClicked = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_log = findViewById(R.id.button_signin)

//
//        // Inicjalizacja elementów interfejsu użytkownika
//        userInput = findViewById(R.id.inputName)
//        button = findViewById(R.id.button)
//        textView = findViewById(R.id.printText)
//
//        textView.text = ""
//        textView.movementMethod = ScrollingMovementMethod()
//
//        nextViewButton = findViewById(R.id.nextView)
        }
    }


