package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DoctorVisitsAdapter(private val doctors: List<DoctorVisit>, private val onDoctorCheckChange: (DoctorVisit) -> Unit) : RecyclerView.Adapter<DoctorVisitsAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorVisitName: TextView = itemView.findViewById(R.id.doctorvisit)
        val doctorCheckbox: CheckBox = itemView.findViewById(R.id.doctorCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_item, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.doctorCheckbox.text = doctor.doctorName
        holder.doctorCheckbox.isChecked = doctor.isChecked
        holder.doctorCheckbox.setOnCheckedChangeListener { _, isChecked ->
            doctor.isChecked = isChecked
            onDoctorCheckChange(doctor)
            // Update doctor visit status in the database
        }
    }

    override fun getItemCount() = doctors.size
}
