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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medicine_item, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(medicines[position], onMedicineChecked)
    }

    override fun getItemCount(): Int = medicines.size

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicineName: TextView = itemView.findViewById(R.id.medicineName)
        private val medicineDose: TextView = itemView.findViewById(R.id.medicineDose)
        private val medicineTime: TextView = itemView.findViewById(R.id.medicineTime)
        private val checkBox: CheckBox = itemView.findViewById(R.id.medicineCheckBox)

        fun bind(medicine: Medicine, onMedicineChecked: (Medicine) -> Unit) {
            medicineName.text = medicine.name
            medicineDose.text = "Dawka: ${medicine.dose}"
            medicineTime.text = "Czas: ${medicine.time}"
            checkBox.isChecked = medicine.isChecked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                medicine.isChecked = isChecked
                onMedicineChecked(medicine)
            }
        }
    }
}
