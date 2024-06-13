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
//class SymptomsAdapter(private var symptoms: List<Symptom>, private val onSymptomCheckChange: (Symptom, Boolean) -> Unit) : RecyclerView.Adapter<SymptomsAdapter.ViewHolder>() {
//
//    class ViewHolder(val binding: SymptomItemBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = SymptomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val symptom = symptoms[position]
//        holder.binding.checkbox.text = symptom.name
//        holder.binding.checkbox.isChecked = symptom.isChecked
//        holder.binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
//            symptoms[position].isChecked = isChecked
//            onSymptomCheckChange(symptom, isChecked)
//        }
//    }
//
//    override fun getItemCount() = symptoms.size
//
//    // Adding a public method to access symptoms safely
//    fun getSymptoms(): List<Symptom> {
//        return symptoms
//    }
//}
