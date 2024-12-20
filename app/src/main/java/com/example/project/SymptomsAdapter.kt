package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.Symptom

/**
 * `SymptomsAdapter` is an adapter class for displaying a list of symptoms in a RecyclerView.
 * Each symptom can be checked or unchecked, and when its state changes, a callback is triggered.
 *
 * @param symptoms The list of symptoms to be displayed.
 * @param onSymptomCheckChange The callback that is triggered when a symptom's checked state changes.
 */
class SymptomsAdapter(
    private val symptoms: List<Symptom>,
    private val onSymptomCheckChange: (Symptom, Boolean) -> Unit
) : RecyclerView.Adapter<SymptomsAdapter.SymptomViewHolder>() {

    /**
     * `SymptomViewHolder` is the view holder class for each symptom item.
     * It holds a reference to the CheckBox widget that represents the symptom.
     *
     * @param itemView The view for the individual symptom item.
     */
    class SymptomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.symptomCheckbox)
    }

    /**
     * Creates a new view holder when needed.
     * Inflates the symptom_item layout to create individual symptom items.
     *
     * @param parent The parent ViewGroup that the new view will be attached to.
     * @param viewType The view type of the new view.
     * @return A new SymptomViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.symptom_item, parent, false)
        return SymptomViewHolder(view)
    }

    /**
     * Binds data to the view holder.
     * Updates the CheckBox state and sets a listener to track changes in the symptom's checked state.
     *
     * @param holder The SymptomViewHolder to bind data to.
     * @param position The position of the item in the adapter's data set.
     */
    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        val symptom = symptoms[position]
        holder.checkBox.text = symptom.name
        holder.checkBox.isChecked = symptom.isChecked
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            symptom.isChecked = isChecked
            onSymptomCheckChange(symptom, isChecked)
        }
    }

    /**
     * Returns the list of symptoms.
     *
     * @return A list of all symptoms.
     */
    fun getSymptoms(): List<Symptom> {
        return symptoms}

    /**
     * Returns the total number of items in the adapter.
     *
     * @return The size of the symptoms list.
     */
    override fun getItemCount() = symptoms.size
}
