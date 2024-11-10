
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.DoctorVisit1
import com.example.project.R

class DoctorVisitsAdapter1(
    private val visitsList: List<DoctorVisit1>,
    private val onItemClick: (DoctorVisit1) -> Unit
) : RecyclerView.Adapter<DoctorVisitsAdapter1.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_visit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val visit = visitsList[position]
        holder.bind(visit)
    }

    override fun getItemCount(): Int {
        return visitsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val doctorNameTextView: TextView = itemView.findViewById(R.id.doctorNameTextView)
        private val visitDateTextView: TextView = itemView.findViewById(R.id.visitDateTextView)

        fun bind(visit: DoctorVisit1) {
            doctorNameTextView.text = visit.doctorName
            visitDateTextView.text = visit.visitDate

            itemView.setOnClickListener {
                onItemClick.invoke(visit)
            }
        }
    }
}
