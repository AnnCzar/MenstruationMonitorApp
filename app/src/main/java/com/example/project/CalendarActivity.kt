package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R.*
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * CalendarActivity is the main screen for displaying the user's menstrual and ovulation calendar.
 * This activity connects with Firebase Firestore to retrieve and display relevant data such as period,
 * ovulation dates, and mucus observations.
 */
class CalendarActivity : AppCompatActivity() {

    private lateinit var calendar: CompactCalendarView
    private lateinit var calendarSettingButton: ImageButton
    private lateinit var calendarAcountButton: ImageButton
    private lateinit var homeButtonCalendar: ImageButton
    private lateinit var monthNameTextView: TextView

    private lateinit var imageView: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView4: ImageView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var textView5: TextView
    private lateinit var pegnancyMucusBAsed: TextView
    private lateinit var userId: String
    private lateinit var db: FirebaseFirestore
    private lateinit var textMucus: TextView


    /**
     * Called when the activity is created.
     * Initializes views, sets up listeners, and fetches user data related to cycles and mucus.
     */
    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.calendar)
        calendar = findViewById(id.calendarView)
        calendarSettingButton = findViewById(id.calendarSettingButton)
        calendarAcountButton = findViewById(id.calendarAcountButton)
        homeButtonCalendar = findViewById(id.homeButtonCalendar)
        imageView = findViewById(id.imageView)
        imageView2 = findViewById(id.imageView2)
        imageView3 = findViewById(id.imageView3)
        imageView4 = findViewById(id.imageView4)
        textView2 = findViewById(id.textView2)
        textView3 = findViewById(id.textView3)
        textView4 = findViewById(id.textView4)
        textView5 = findViewById(id.textView5)
        monthNameTextView = findViewById(id.monthNameTextView)
        pegnancyMucusBAsed = findViewById(id.pegnancyMucusBAsed)
        textMucus = findViewById(id.textMucus)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        fetchMucus()
        fetchUserPeriodAndOvulationDates()

        /**
         * This method is triggered when a user clicks on a specific date in the calendar.
         * It fetches mucus data for the selected date and opens a new activity for that day.
         */
        calendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                dateClicked?.let {
                    val selectedDate = dateClicked.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    fetchMucusForDate(selectedDate)
                    openDayActivity(userId, selectedDate)
                }
            }

            /**
             * This method is triggered when the user scrolls the calendar to a new month.
             * It updates the displayed month name at the top of the screen.
             */
            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                updateMonthName(firstDayOfNewMonth)
            }
        })

        calendarAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        calendarSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }
        homeButtonCalendar.setOnClickListener {
            val userRef = db.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { user ->
                    if (user != null) {
                        val statusPregnancy = user.getBoolean("statusPregnancy")
                        if (statusPregnancy != null) {
                            if (!statusPregnancy) {
                                openMainWindowPeriodActivity(userId)
                            } else {
                                openMainWindowPregnancyActivity(userId)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /**
     * Called when the activity is resumed.
     * Checks the pregnancy status of the user to adjust the UI accordingly.
     */
    override fun onResume() {
        super.onResume()
        checkPregnantStatus()
    }

    /**
     * Checks if the user is pregnant and adjusts the UI to show or hide pregnancy-related elements.
     */
    private fun checkPregnantStatus() {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { user ->
                if (user != null) {
                    val statusPregnancy = user.getBoolean("statusPregnancy")
                    if (statusPregnancy != null) {
                        if (!statusPregnancy) {
                            textView2.visibility = TextView.VISIBLE
                            textView3.visibility = TextView.VISIBLE
                            textView4.visibility = TextView.VISIBLE
                            textView5.visibility = TextView.VISIBLE
                            imageView.visibility = ImageView.VISIBLE
                            imageView2.visibility = ImageView.VISIBLE
                            imageView3.visibility = ImageView.VISIBLE
                            imageView4.visibility = ImageView.VISIBLE
                            textMucus.visibility = ImageView.VISIBLE
                            pegnancyMucusBAsed.visibility = ImageView.VISIBLE
                        } else {
                            textView2.visibility = TextView.GONE
                            textView3.visibility = TextView.GONE
                            textView4.visibility = TextView.GONE
                            textView5.visibility = TextView.GONE
                            imageView.visibility = ImageView.GONE
                            imageView2.visibility = ImageView.GONE
                            imageView3.visibility = ImageView.GONE
                            imageView4.visibility = ImageView.GONE
                            textMucus.visibility = ImageView.GONE
                            pegnancyMucusBAsed.visibility = ImageView.GONE

                        }
                    }
                }
            }
    }

    /**
     * Fetches the user's period and ovulation dates from Firestore and marks the respective days on the calendar.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchUserPeriodAndOvulationDates() {
        val cyclesRef = db.collection("users").document(userId).collection("cycles")

        cyclesRef.get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val userRef = db.collection("users").document(userId)
                    userRef.get().addOnSuccessListener { user ->

                        for (document in result) {
                            val startDateString = document.getString("startDate")
                            val endDateString = document.getString("endDate")
                            val ovulationDateString = document.getString("nextOvulationDate")

                            if (startDateString != null && endDateString != null && ovulationDateString != null) {
                                val startDate = LocalDate.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE)
                                val endDate = LocalDate.parse(endDateString, DateTimeFormatter.ISO_LOCAL_DATE)
                                val ovulationDate = LocalDate.parse(ovulationDateString, DateTimeFormatter.ISO_LOCAL_DATE)

                                val periodStart = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                                val periodEnd = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                                val ovulationDay = Date.from(ovulationDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

                                val eventDecorator = EventDecorator(calendar)
                                eventDecorator.markPeriodDays(periodStart, periodEnd)
                                eventDecorator.markOvulation(ovulationDay)
                                eventDecorator.markFertilityDays(ovulationDate)

                            } else {
                                Toast.makeText(this, "Niekompletne dane cyklu dla jednego z dokumentów.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        calendar.invalidate()
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, "Błąd podczas pobierania danych użytkownika: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Nie znaleziono danych cyklu.", Toast.LENGTH_SHORT).show()
                }
                fetchDoctorVisits()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd podczas pobierania danych cyklu: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    /**
     * Fetches the user's doctor visits from Firestore and marks the respective days on the calendar.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDoctorVisits() {
        db.collection("users").document(userId).collection("doctorVisits")
            .get()
            .addOnSuccessListener { result ->
                Log.d("DoctorVisitsActivity", "Liczba dokumentów: ${result.documents.size}")
                for (document in result) {
                    val visitDateString = document.getString("visitDate")
                    if (visitDateString != null) {
                        val visitDate = LocalDate.parse(visitDateString, DateTimeFormatter.ISO_LOCAL_DATE)

                        val visitDay = Date.from(visitDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                        val eventDecorator = EventDecorator(calendar)
                        eventDecorator.doctorVisits(visitDay)
                    } else {
                        Log.e("DoctorVisitsActivity", "Visit date is missing for document: ${document.id}")
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching doctor visits: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Fetches mucus data for a specific date and updates the UI with the corresponding description.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchMucusForDate(date: LocalDate) {
        val mucusDescriptions = mapOf(
            "Brak śluzu" to "Nie zaobserwowano śluzu.",
            "Lepki" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Gęsty" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Kleisty" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Białe lub lekko żółte zabarwienie" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Mętny" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Brązowy" to "Niskie szanse. Może sugerować zbliżający się okres lub sygnalizować zapłodnienie.",
            "Przejrzysty lekko ciągnący się" to "Wysokie szanse na zajście w ciążę.",
            "Wodnisty" to "Wysokie szanse na zajście w ciążę.",
            "Rozciągliwy, o dużej wilgotności (białko jajka)" to "Bardzo wysokie szanse na zapłodnienie, śluz charakterystyczny dla owulacji."
        )
        val dateString = date.format(DateTimeFormatter.ISO_DATE)
        db.collection("users").document(userId).collection("dailyInfo")
            .document(dateString)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val mucusType = document.getString("mucusType")
                    val description = mucusDescriptions[mucusType] ?: "Brak informacji o śluzie."
                    pegnancyMucusBAsed.text = description
                } else {
                    pegnancyMucusBAsed.text = "Brak danych na ten dzień."
                }
            }
            .addOnFailureListener { exception ->
                pegnancyMucusBAsed.text = "Błąd pobierania danych: ${exception.message}"
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchMucus() {
        val mucusDescriptions = mapOf(
            "Brak śluzu" to "Nie zaobserwowano śluzu.",
            "Lepki" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Gęsty" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Kleisty" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Białe lub lekko żółte zabarwienie" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Mętny" to "Bardzo niskie szanse na zapłodnienie, sugeruje zbliżanie się okresu.",
            "Brązowy" to "Niskie szanse. Może sugerować zbliżający się okres lub sygnalizować zapłodnienie.",
            "Przejrzysty lekko ciągnący się" to "Wysokie szanse na zajście w ciążę.",
            "Wodnisty" to "Wysokie szanse na zajście w ciążę.",
            "Rozciągliwy, o dużej wilgotności (białko jajka)" to "Bardzo wysokie szanse na zapłodnienie, śluz charakterystyczny dla owulacji."
        )
        val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        db.collection("users").document(userId).collection("dailyInfo")
            .document(todayDate)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val mucusType = document.getString("mucusType")
                    if (mucusType != null && mucusDescriptions.containsKey(mucusType)) {
                        val description = mucusDescriptions[mucusType]
                        runOnUiThread {
                            pegnancyMucusBAsed.text = "$description"
                            Log.d("FetchMucus", "Mucus Type: $mucusType")
                            Log.d("FetchMucus", "Description: ${mucusDescriptions[mucusType]}")

                        }
                    } else {
                        runOnUiThread {
                            pegnancyMucusBAsed.text = "\nBrak informacji o śluzie."
                        }
                    }
                } else {
                    runOnUiThread {
                        pegnancyMucusBAsed.text = "\nBrak danych na ten dzień."
                    }
                }
            }
            .addOnFailureListener { exception ->
                runOnUiThread {
                    pegnancyMucusBAsed.text = "Błąd pobierania danych: ${exception.message}"
                }
            }
    }

    /**
     * Updates the displayed month name on the calendar view when the user scrolls to a new month.
     *
     * @param firstDayOfNewMonth The first day of the newly selected month.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMonthName(date: Date) {
        val dateFormat = SimpleDateFormat("LLLL yyyy", Locale("pl"))
        val monthName = dateFormat.format(date)
        monthNameTextView.text = monthName.capitalize(Locale("pl"))
    }

    /**
     * Opens the activity for the specific day.
     *
     * @param userId The unique ID of the user.
     * @param selectedDate The selected date to be displayed in the day activity.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openDayActivity(userId: String, date: LocalDate) {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val pregnancyStatus = document.getBoolean("statusPregnancy") ?: false
                    val intent = if (pregnancyStatus) {
                        Intent(this, MainWindowPregnancyActivity::class.java)
                    } else {
                        Intent(this, MainWindowPeriodActivity::class.java)
                    }
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("SELECTED_DATE", date.format(DateTimeFormatter.ISO_DATE))

                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Nie znaleziono użytkownika", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Opens the settings window activity.
     */
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    /**
     * Opens the account window activity.
     */
    private fun openAccountWindowActivity(userId: String){
        val intent = Intent(this, AccountWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    /**
     * Opens the main window for the period activity.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }

    /**
     * Opens the main window for the pregnancy activity.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }
}
