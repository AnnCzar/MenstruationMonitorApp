package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class AdditionalInformationActivity : AppCompatActivity(){

    private lateinit var imageButtonHappy: ImageButton
    private lateinit var imageButtonNeutral: ImageButton
    private lateinit var imageButtonSad: ImageButton

    private lateinit var enterWeight: EditText
    private lateinit var addInfoEnterTemperature: EditText

    private lateinit var checkBoxHeadache: CheckBox
    private lateinit var checkBoxAcne: CheckBox
    private lateinit var checkBoxStomachache: CheckBox
    private lateinit var checkBoxMastalgia: CheckBox
    private lateinit var checkBoxDizziness: CheckBox
    private lateinit var checkBoxFever: CheckBox
    private lateinit var checkBoxBackache: CheckBox
    private lateinit var checkBoxBedabbling: CheckBox
    private lateinit var checkBoxNerves: CheckBox
    private lateinit var checkBoxHunger: CheckBox
    private lateinit var checkBoxDiarrhoea: CheckBox
    private lateinit var checkBoxConstipation: CheckBox

    private lateinit var buttonSaveAddInfo : Button

    private lateinit var addInfoSettingAcountButton: ImageButton
    private lateinit var addInfoSettingButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.additional_information)

        imageButtonHappy = findViewById(R.id.imageButtonHappy)
        imageButtonNeutral = findViewById(R.id.imageButtonNeutral)
        imageButtonSad = findViewById(R.id.imageButtonSad)

        enterWeight = findViewById(R.id.enterWeight)
        addInfoEnterTemperature = findViewById(R.id.addInfoEnterTemperature)

        buttonSaveAddInfo = findViewById((R.id.buttonSaveAddInfo))

        checkBoxHeadache = findViewById(R.id.checkBox_headache)
        checkBoxAcne = findViewById(R.id.checkBox_acne)
        checkBoxStomachache = findViewById(R.id.checkBox_stomachahe)
        checkBoxMastalgia = findViewById(R.id.checkBox_mastalgia)
        checkBoxDizziness = findViewById(R.id.checkBox_dizziness)
        checkBoxFever = findViewById(R.id.checkBox_fever)
        checkBoxBackache = findViewById(R.id.checkBox_bachache)
        checkBoxBedabbling = findViewById(R.id.checkBox_bedabbling)
        checkBoxNerves = findViewById(R.id.checkBox_nerves)
        checkBoxHunger = findViewById(R.id.checkBox_hunger)
        checkBoxDiarrhoea = findViewById(R.id.checkBox_diarrhoea)
        checkBoxConstipation = findViewById(R.id.checkBox_constipation)

        addInfoSettingAcountButton = findViewById(R.id.addInfoSettingAcountButton)
        addInfoSettingButton = findViewById(R.id.addInfoSettingButton)



        addInfoSettingButton.setOnClickListener {
            openSettingsWindowActivity()

        }

        addInfoSettingAcountButton.setOnClickListener {
            openAccountWindowActivity()

        }



        buttonSaveAddInfo.setOnClickListener{

        }
    // Set onClickListener for each CheckBox
    checkBoxHeadache.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxAcne.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxStomachache.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxMastalgia.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxDizziness.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxFever.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxBackache.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxBedabbling.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxNerves.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxHunger.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxDiarrhoea.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    checkBoxConstipation.setOnCheckedChangeListener { buttonView, isChecked ->
        // Do something when CheckBox state changes
    }

    imageButtonHappy.setOnClickListener {

        }

    imageButtonSad.setOnClickListener {

        }

    imageButtonNeutral.setOnClickListener {
        }

    enterWeight.setOnClickListener {

        }
    addInfoEnterTemperature.setOnClickListener {
        }

    }
    private fun openSettingsWindowActivity() {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        startActivity(intent)
    }

    private fun openAccountWindowActivity(){
        val intent = Intent(this, AccountWindowActivity::class.java)
        startActivity(intent)
    }
}
