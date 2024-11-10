package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.Symptom

class SymptomsAdapter(
    private val symptoms: List<Symptom>,
    private val onSymptomCheckChange: (Symptom, Boolean) -> Unit
) : RecyclerView.Adapter<SymptomsAdapter.SymptomViewHolder>() {

    class SymptomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.symptomCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.symptom_item, parent, false)
        return SymptomViewHolder(view)
    }

    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        val symptom = symptoms[position]
        holder.checkBox.text = symptom.name
        holder.checkBox.isChecked = symptom.isChecked
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onSymptomCheckChange(symptom, isChecked)
        }
    }
    fun getSymptoms(): List<Symptom> {
        return symptoms}

    override fun getItemCount() = symptoms.size
}
