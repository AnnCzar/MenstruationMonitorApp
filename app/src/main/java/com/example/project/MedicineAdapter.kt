package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for displaying a list of medicines in a RecyclerView.
 *
 * @param medicines The list of medicines to display.
 * @param onMedicineChecked Callback function that will be called when a medicine's checkbox is checked or unchecked.
 */
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

    /**
     * ViewHolder class for displaying individual medicine items.
     *
     * @param itemView The view associated with this ViewHolder.
     */
    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicineName: TextView = itemView.findViewById(R.id.medicineName)
        private val medicineDose: TextView = itemView.findViewById(R.id.medicineDose)
        private val medicineTime: TextView = itemView.findViewById(R.id.medicineTime)
        private val checkBox: CheckBox = itemView.findViewById(R.id.medicineCheckBox)

        /**
         * Binds a medicine to the ViewHolder.
         *
         * @param medicine The medicine to bind.
         * @param onMedicineChecked Callback function that will be called when the checkbox state changes.
         */
        fun bind(medicine: Medicine, onMedicineChecked: (Medicine) -> Unit) {
            medicineName.text = medicine.name
            medicineDose.text = "Dawka: ${medicine.dose}"
            medicineTime.text = "Czas: ${medicine.time}"


            // Disable listener temporarily to avoid triggering during initialization
            checkBox.setOnCheckedChangeListener(null)

            // Reflect database state in UI
            checkBox.isChecked = medicine.isChecked


            checkBox.setOnCheckedChangeListener { _, isChecked ->
                medicine.isChecked = isChecked
                onMedicineChecked(medicine)
            }
        }
    }
}
