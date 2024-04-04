package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class AdditionalInformationActivity : AppCompatActivity(){

    private lateinit var imageButtonHappy: ImageButton
    private lateinit var imageButtonNeutral: ImageButton
    private lateinit var imageButtonSad: ImageButton
    private lateinit var enterWeight: EditText
    private lateinit var enterTemperature: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.additional_information)

        imageButtonHappy = findViewById(R.id.imageButtonHappy)
        imageButtonNeutral = findViewById(R.id.imageButtonNeutral)
        imageButtonSad = findViewById(R.id.imageButtonSad)
        enterWeight = findViewById(R.id.enterWeight)
        enterTemperature = findViewById(R.id.enterTemperature)

        imageButtonHappy.setOnClickListener {

        }

        imageButtonSad.setOnClickListener {

        }

        imageButtonNeutral.setOnClickListener {

        }

        enterWeight.setOnClickListener {

        }

        enterTemperature.setOnClickListener {
        }

    }
}
