package com.example.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicineAdapterModify(
    private val medicineList: List<MedicineList>,
    private val onEditClick: (MedicineList) -> Unit,
    private val onDeleteClick: (MedicineList) -> Unit
) : RecyclerView.Adapter<MedicineAdapterModify.MedicineViewHolder>() {

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName: TextView = itemView.findViewById(R.id.medicineName)
        val medicineDose: TextView = itemView.findViewById(R.id.doseMedicine)
        val medicineTime: TextView = itemView.findViewById(R.id.timeMedicine)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_item_modify, parent, false)
        return MedicineViewHolder(view)
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicineList[position]
        holder.medicineName.text = medicine.name
        holder.medicineDose.text = ("Dawka: ${medicine.dose}")
        holder.medicineTime.text = "Pora:  ${medicine.time}"
        holder.editButton.setOnClickListener {
            onEditClick(medicine)
        }
        holder.deleteButton.setOnClickListener {
            onDeleteClick(medicine)
        }
    }
    override fun getItemCount(): Int = medicineList.size
}




