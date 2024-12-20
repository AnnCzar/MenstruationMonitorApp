import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.DoctorVisit
import com.example.project.R

/**
 * Adapter for displaying a list of doctor visits in a RecyclerView.
 *
 * @param doctorVisits List of [DoctorVisit] objects to display.
 * @param onEditClick Lambda function to handle edit button clicks.
 * @param onDeleteClick Lambda function to handle delete button clicks.
 * @param onMapClick Lambda function to handle map button clicks.
 */
class DoctorVisitAdapter(
    private val doctorVisits: List<DoctorVisit>,
    private val onEditClick: (DoctorVisit) -> Unit,
    private val onDeleteClick: (DoctorVisit) -> Unit,
    private val onMapClick: (DoctorVisit) -> Unit
) : RecyclerView.Adapter<DoctorVisitAdapter.DoctorVisitViewHolder>() {

    /**
     * ViewHolder for displaying individual doctor visit information.
     *
     * @param itemView The view for the individual item.
     */
    class DoctorVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorVisitTextView: TextView = itemView.findViewById(R.id.doctorvisit)
        val visitDateTextView: TextView = itemView.findViewById(R.id.visitDateAndTime)
        val extraInfo: TextView = itemView.findViewById(R.id.extraInfo)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val visitOnMap: Button = itemView.findViewById(R.id.visitOnMap)
    }

    /**
     * Creates a new [DoctorVisitViewHolder] when there are no existing view holders available for reuse.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of [DoctorVisitViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVisitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_item, parent, false)
        return DoctorVisitViewHolder(view)
    }

    /**
     * Binds data to the specified [DoctorVisitViewHolder].
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorVisitViewHolder, position: Int) {
        val visit = doctorVisits[position]
        holder.doctorVisitTextView.text = visit.doctorName
        holder.visitDateTextView.text = ("Data wizyty: ${visit.visitDate}, godzina: ${visit.time} ")
        holder.extraInfo.text = "Dodatkowe informacje:  ${visit.extraInfo}"
        holder.editButton.setOnClickListener {
            onEditClick(visit)
        }
        holder.visitOnMap.setOnClickListener {
            onMapClick(visit)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(visit)
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The number of items in the adapter's data set.
     */
    override fun getItemCount(): Int = doctorVisits.size
}
