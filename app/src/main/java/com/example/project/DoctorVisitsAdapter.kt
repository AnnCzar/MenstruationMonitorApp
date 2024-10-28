import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.DoctorVisit
import com.example.project.R

class DoctorVisitAdapter(
    private val doctorVisits: List<DoctorVisit>,
    private val onEditClick: (DoctorVisit) -> Unit,
    private val onDeleteClick: (DoctorVisit) -> Unit
) : RecyclerView.Adapter<DoctorVisitAdapter.DoctorVisitViewHolder>() {

    class DoctorVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorVisitTextView: TextView = itemView.findViewById(R.id.doctorvisit)
        val visitDateTextView: TextView = itemView.findViewById(R.id.visitDateAndTime)
        val extraInfo: TextView = itemView.findViewById(R.id.extraInfo)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVisitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_item, parent, false)
        return DoctorVisitViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorVisitViewHolder, position: Int) {
        val visit = doctorVisits[position]
        holder.doctorVisitTextView.text = visit.doctorName
        holder.visitDateTextView.text = ("Data wizyty: ${visit.visitDate}, godzina: ${visit.time} ")
        holder.extraInfo.text = "Dodatkowe informacje:  ${visit.extraInfo}"
        holder.editButton.setOnClickListener {
            onEditClick(visit)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(visit)
        }
    }

    override fun getItemCount(): Int = doctorVisits.size
}
