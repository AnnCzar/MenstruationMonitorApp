package com.example.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for displaying a list of medicines with edit and delete buttons in a RecyclerView.
 *
 * @param medicineList The list of medicines to display.
 * @param onEditClick Callback function that will be called when the edit button for a medicine is clicked.
 * @param onDeleteClick Callback function that will be called when the delete button for a medicine is clicked.
 */
class MedicineAdapterModify(
    private val medicineList: List<MedicineList>,
    private val onEditClick: (MedicineList) -> Unit,
    private val onDeleteClick: (MedicineList) -> Unit
) : RecyclerView.Adapter<MedicineAdapterModify.MedicineViewHolder>() {

    /**
     * ViewHolder class for displaying individual medicine items with modification options.
     *
     * @param itemView The view associated with this ViewHolder.
     */
    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName: TextView = itemView.findViewById(R.id.medicineName)
        val medicineDose: TextView = itemView.findViewById(R.id.doseMedicine)
        val medicineTime: TextView = itemView.findViewById(R.id.timeMedicine)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    /**
     * Creates a new ViewHolder when needed.
     *
     * @param parent The parent view group.
     * @param viewType The view type of the new View.
     * @return A new `MedicineViewHolder` instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_item_modify, parent, false)
        return MedicineViewHolder(view)
    }

    /**
     * Binds a medicine item to the ViewHolder, setting its data and handling interactions.
     *
     * @param holder The ViewHolder that will display the medicine.
     * @param position The position of the item in the list.
     */
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

    /**
     * Returns the total number of items in the list.
     *
     * @return The number of medicines in the list.
     */
    override fun getItemCount(): Int = medicineList.size
}




