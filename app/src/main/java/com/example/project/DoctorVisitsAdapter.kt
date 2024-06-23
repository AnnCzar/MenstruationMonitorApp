import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.DoctorVisit
import com.example.project.R

// Adapter for RecyclerView
class DoctorVisitAdapter(
    private val visits: List<DoctorVisit>,
    private val onVisitClick: (DoctorVisit) -> Unit
) : RecyclerView.Adapter<DoctorVisitAdapter.DoctorVisitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVisitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_visit, parent, false)
        return DoctorVisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorVisitViewHolder, position: Int) {
        val visit = visits[position]
        holder.bind(visit)
        holder.itemView.setOnClickListener { onVisitClick(visit) }
    }

    override fun getItemCount(): Int = visits.size

    class DoctorVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val doctorNameTextView: TextView = itemView.findViewById(R.id.doctorNameTextView)
        private val visitDateTextView: TextView = itemView.findViewById(R.id.visitDateTextView)

        fun bind(visit: DoctorVisit) {
            doctorNameTextView.text = visit.doctorName
            visitDateTextView.text = visit.visitDate
        }
    }
}
