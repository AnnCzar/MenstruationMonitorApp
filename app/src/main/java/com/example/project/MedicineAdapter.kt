package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicineAdapter(
    private val medicines: List<Medicine>,
    private val onMedicineChecked: (Medicine) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName: TextView = itemView.findViewById(R.id.medicineName)
        val medicineCheckBox: CheckBox = itemView.findViewById(R.id.medicineCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_item, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.medicineName.text = medicine.name
        holder.medicineCheckBox.isChecked = medicine.isChecked

        holder.medicineCheckBox.setOnCheckedChangeListener { _, isChecked ->
            medicine.isChecked = isChecked
            onMedicineChecked(medicine)
        }
    }

    override fun getItemCount(): Int {
        return medicines.size
    }
}
