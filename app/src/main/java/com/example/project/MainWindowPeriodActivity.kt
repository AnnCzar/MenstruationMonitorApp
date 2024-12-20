package com.example.project

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.graphics.Color
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

/**
 * Main activity for managing the period tracking interface.
 */
class MainWindowPeriodActivity : AppCompatActivity() {
    private lateinit var currentDateTextPeriod: TextView
    private lateinit var daysLeftPeriod: TextView
    private lateinit var daysLeftOwulation: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var endPeriodButton: Button
    private lateinit var toCalendarButtonPeriod: ImageButton
    private lateinit var begginingPeriodButton: Button
    private lateinit var mainWindowPeriodSettingButton: ImageButton
    private lateinit var mainWindowPeriodAcountButton: ImageButton
    private lateinit var selectedDate: LocalDate
    private lateinit var additionalInfoPeriod: Button
    private lateinit var cycleDayPeriod: TextView

    private lateinit var doctorRecyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorVisitAdapter


    /**
     * Adapter for displaying a list of doctor visits in a RecyclerView.
     */
    class DoctorVisitAdapter(
        private val visits: List<DoctorVisit>,
    ) : RecyclerView.Adapter<DoctorVisitAdapter.DoctorVisitViewHolder>() {


        /**
         * Inflates the layout for individual items in the RecyclerView.
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVisitViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return DoctorVisitViewHolder(view)
        }

        /**
         * Binds data to the ViewHolder for each item in the list.
         */
        override fun onBindViewHolder(holder: DoctorVisitViewHolder, position: Int) {
            val visit = visits[position]
            holder.bind(visit)
        }

        /**
         * Returns the total number of items in the list.
         */
        override fun getItemCount(): Int = visits.size


        /**
         * ViewHolder for displaying individual doctor visit items.
         */
        class DoctorVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(android.R.id.text1)


            /**
             * Binds a DoctorVisit object to the text view.
             */
            fun bind(visit: DoctorVisit) {

                textView.text =
                    "${visit.doctorName} - Godzina: ${visit.time}\nInformacje: ${visit.extraInfo}"
                textView.setTextColor(Color.BLACK)

            }
        }
    }

    private val doctors = mutableListOf<DoctorVisit>()
    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private val medicines = mutableListOf<Medicine>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_period)
        createNotificationChannel()
        createNotificationChannelPeriod()
        userId = intent.getStringExtra("USER_ID") ?: ""

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(
                    this@MainWindowPeriodActivity,
                    "Cofanie jest wyłączone!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        db = FirebaseFirestore.getInstance()

        checkDate()
        fetchPeriodStatus()

        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
        medicineRecyclerView.layoutManager = LinearLayoutManager(this)
        medicineAdapter = MedicineAdapter(medicines) { medicine ->
            saveMedicineCheckStatus(medicine, selectedDate)
        }
        medicineRecyclerView.adapter = medicineAdapter

        doctorRecyclerView = findViewById(R.id.doctorsRecyclerView)
        doctorRecyclerView.layoutManager = LinearLayoutManager(this)
        currentDateTextPeriod = findViewById(R.id.currentDateTextPeriod)
        daysLeftPeriod = findViewById(R.id.daysLeftPeriod)
        daysLeftOwulation = findViewById(R.id.daysLeftOwulation)
        toCalendarButtonPeriod = findViewById(R.id.toCalendarButtonPeriod)
        begginingPeriodButton = findViewById(R.id.begginingPeriodButton)
        endPeriodButton = findViewById(R.id.endPeriodButton)
        additionalInfoPeriod = findViewById(R.id.additionalInfoPeriod)
        cycleDayPeriod = findViewById(R.id.cycleDayPeriod)
        mainWindowPeriodSettingButton = findViewById(R.id.mainWindowPeriodSettingButton)
        mainWindowPeriodAcountButton = findViewById(R.id.mainWindowPeriodAcountButton)

        currentDateTextPeriod.text = selectedDate.toString()

        doctorAdapter = DoctorVisitAdapter(doctors)
        doctorRecyclerView.adapter = doctorAdapter
        fetchMedicines()
        fetchLatestCycleData()
        fetchDoctorVisits()

        endPeriodButton.visibility = Button.GONE
        begginingPeriodButton.visibility = Button.GONE



        toCalendarButtonPeriod.setOnClickListener {
            openCalendarActivity(userId)
        }
        additionalInfoPeriod.setOnClickListener {
            val selectedDate1 = currentDateTextPeriod.text.toString()
            openAdditionalInformationActivity(userId, selectedDate1)
        }

        begginingPeriodButton.setOnClickListener {
            openPeriodBeggining(userId)
            updateButtonVisibility(true)
        }
        endPeriodButton.setOnClickListener {
            openPeriodEnding(userId)
            updateButtonVisibility(false)
        }
        mainWindowPeriodAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }
        mainWindowPeriodSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }
        fetchTodaysCycleDay()

        scheduleNotification()

        FirebaseMessaging.getInstance().subscribeToTopic("user_${userId}")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d("FCM", msg)
            }
        db.collection("Chats")
            .whereEqualTo("receiver", userId)
            .whereEqualTo("isseen", false)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed: ${e.message}", e)
                    return@addSnapshotListener
                }
                val latestMessage = snapshots?.documents
                    ?.map { it.toObject(Message::class.java) }
                    ?.maxByOrNull { it?.timestamp ?: 0L }
                if (latestMessage != null) {
                    sendNotification(latestMessage)
                }
            }

                FirebaseMessaging.getInstance().subscribeToTopic("user_${userId}")
                    .addOnCompleteListener { task ->
                        val msg = if (task.isSuccessful) "Subscribed" else "Subscription failed"
                        Log.d("FCM", msg)
                    }

        db.collection("Chats")
            .whereEqualTo("receiver", userId)
            .whereEqualTo("isseen", false)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed: ${e.message}", e)
                    return@addSnapshotListener
                }

                val latestMessage = snapshots?.documents
                    ?.mapNotNull { it.toObject(Message::class.java) }
                    ?.maxByOrNull { it.timestamp ?: 0L }

                if (latestMessage != null) {
                    sendNotification(latestMessage)
                }
            }}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        checkDate()
        fetchMedicines()
        fetchPeriodStatus()
        fetchLatestCycleData()
    }

    /**
     * Checks and sets the selected date from the intent or defaults to the current date.
     * If no date is provided via intent, it initializes the selected date to the current day.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDate() {
        val dateString = intent?.getStringExtra("SELECTED_DATE")
        selectedDate = if (dateString.isNullOrEmpty()) {
            LocalDate.now()
        } else {
            LocalDate.parse(dateString)
        }
    }

    /**
     * Updates the visibility of the period-related buttons.
     * Shows the "Start Period" button if the period has not started,
     * and shows the "End Period" button if the period is ongoing.
     *
     * @param isPeriodStarted Boolean indicating if the period has started.
     */
    private fun updateButtonVisibility(isPeriodStarted: Boolean) {
        if(!isPeriodStarted){
            begginingPeriodButton.visibility = Button.VISIBLE
            endPeriodButton.visibility = Button.GONE
        }
        else if(isPeriodStarted) {
            begginingPeriodButton.visibility = Button.GONE
            endPeriodButton.visibility = Button.VISIBLE
        }
    }

    /**
     * Displays the current cycle day on the UI.
     * Updates the cycle day text view with the provided day value.
     *
     * @param cycleDay The day of the menstrual cycle to display.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayCycleDay(cycleDay: Long) {
        Log.d("UI", "Updating UI with cycleDay: $cycleDay")
        runOnUiThread {
            cycleDayPeriod.text = "$cycleDay"
        }
    }


    /**
     * Creates a notification channel for medicine reminders.
     * Required for Android O (API level 26) and above to support notifications.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MedicineReminderChannel"
            val descriptionText = "Channel for Medicine Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MEDICINE_REMINDER_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Creates a notification channel for period reminders.
     * Ensures notifications for period tracking are properly handled.
     */
    private fun createNotificationChannelPeriod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PeriodReminderChannel"
            val descriptionText = "Channel for Period Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("PERIOD_REMINDER_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }





    /**
     * Parses a custom date object into a LocalDate.
     * Supports conversion from a map structure with year, month, and day keys.
     *
     * @param dateValue An object potentially representing a date in a map format.
     * @return A LocalDate if parsing succeeds; null otherwise.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseCustomDate(dateValue: Any?): LocalDate? {
        return try {
            if (dateValue is Map<*, *>) {
                val year = (dateValue["year"] as? Long)?.toInt() ?: return null
                val month = (dateValue["monthValue"] as? Long)?.toInt() ?: return null
                val day = (dateValue["dayOfMonth"] as? Long)?.toInt() ?: return null
                LocalDate.of(year, month, day)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calculates a date based on the cycle day and cycle length.
     *
     * @param lastPeriodDate The date of the last period.
     * @param cycleDay The current day of the cycle.
     * @param cycleLength The total length of the cycle.
     * @return A LocalDate representing the calculated date.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDateFromCycleDay(lastPeriodDate: LocalDate, cycleDay: Int, cycleLength: Int): LocalDate {
        val daysFromStart = (cycleDay - 1) % cycleLength
        return lastPeriodDate.plusDays(daysFromStart.toLong())
    }

    private fun changeButtonVisibility() {
        begginingPeriodButton.visibility = Button.VISIBLE
    }


    /**
     * Calculates the number of days in a given month.
     *
     * @param month A string in the format "YYYY-MM".
     * @return The number of days in the specified month.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDaysInMonth(month: String): Int {
        val year = month.substring(0, 4).toInt()
        val monthValue = month.substring(5, 7).toInt()
        val firstDayOfMonth = LocalDate.of(year, monthValue, 1)
        return firstDayOfMonth.lengthOfMonth()
    }

    /**
     * Calculates the current day in the menstrual cycle.
     *
     * @param lastPeriodDate The start date of the last period.
     * @param tempDate The date for which to calculate the cycle day.
     * @param cycleLength The length of the menstrual cycle in days.
     * @return The day in the cycle or null if the date is invalid.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateCycleDay(lastPeriodDate: LocalDate, tempDate: LocalDate, cycleLength: Int): Int? {
        val daysDifference = java.time.temporal.ChronoUnit.DAYS.between(lastPeriodDate, tempDate).toInt()
        return if (daysDifference >= 0) (daysDifference % cycleLength) + 1 else null
    }

    /**
     * Calculates the median value of a list of integers.
     *
     * @param data The list of integers to process.
     * @return The median value or 0.0 if the list is empty.
     */
    private fun calculateMedian(data: List<Int>): Double {
        if (data.isEmpty()) return 0.0
        val sorted = data.sorted()
        val size = sorted.size
        return if (size % 2 == 0) {
            (sorted[size / 2 - 1] + sorted[size / 2]) / 2.0
        } else {
            sorted[size / 2].toDouble()
        }
    }

    /**
     * Calculates and updates the displayed dates related to the menstrual cycle.
     *
     * @param lastPeriodDate The start date of the last period.
     * @param cycleLength The length of the menstrual cycle in days.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAndDisplayDates(lastPeriodDate: LocalDate, cycleLength: Int) {

        val daysSinceLastPeriod = ChronoUnit.DAYS.between(lastPeriodDate, selectedDate)
        val currentCycleDay = (daysSinceLastPeriod % cycleLength).toInt() + 1

        val daysUntilNextPeriod = if (daysSinceLastPeriod < cycleLength) {
            cycleLength - daysSinceLastPeriod
        } else {
            cycleLength - (daysSinceLastPeriod % cycleLength)
        }

        val nextOvulationDate = lastPeriodDate.plusDays((cycleLength - 14).toLong())

        var daysUntilNextOvulation = ChronoUnit.DAYS.between(selectedDate, nextOvulationDate)
        if (daysUntilNextOvulation < 0) {
            val adjustedOvulationDate = nextOvulationDate.plusDays(cycleLength.toLong())
            daysUntilNextOvulation = ChronoUnit.DAYS.between(selectedDate, adjustedOvulationDate)
        }

        if (daysUntilNextPeriod.toInt() == 1) {
            schedulePeriodNotification()
        }
        Log.d("PeriodReminder", "Days until next period: $daysUntilNextPeriod")

        runOnUiThread {
            daysLeftOwulation.text = daysUntilNextOvulation.toString()
            daysLeftPeriod.text = daysUntilNextPeriod.toString()
            cycleDayPeriod.text = currentCycleDay.toString()
        }
    }

    /**
     * Updates the displayed ovulation date information.
     *
     * @param nextOvulationDate The calculated next ovulation date.
     * @param cycleLength The length of the menstrual cycle in days.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDates(nextOvulationDate: LocalDate, cycleLength: Int) {
        var daysUntilNextOvulation = ChronoUnit.DAYS.between(selectedDate, nextOvulationDate)

        if (daysUntilNextOvulation < 0) {
            val adjustedOvulationDate = nextOvulationDate.plusDays(cycleLength.toLong())
            daysUntilNextOvulation = ChronoUnit.DAYS.between(selectedDate, adjustedOvulationDate)
        }

        runOnUiThread {
            daysLeftOwulation.text = daysUntilNextOvulation.toString()
        }
    }

    /**
     * Displays a short message as a toast.
     *
     * @param message The message to display.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    /**
     * Suppresses the default behavior of the back button press.
     * This method is intentionally left empty to override and disable the default action.
     */
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }

    /**
     * Saves the check status of a specific medicine for the selected date in the Firestore database.
     *
     * @param medicine The medicine whose status is being updated.
     * @param selectedDate The date for which the status is being saved.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMedicineCheckStatus(medicine: Medicine, selectedDate: LocalDate) {
        Log.d("Zapis daty", selectedDate.toString())

        val dateKey = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dailyInfoRef = db.collection("users").document(userId)
            .collection("dailyInfo").document(dateKey)
            .collection("medicines").document(medicine.id)

        dailyInfoRef.set(mapOf("checked" to medicine.isChecked))
            .addOnSuccessListener {

                medicines.find { it.id == medicine.id }?.isChecked = medicine.isChecked
                runOnUiThread { medicineAdapter.notifyDataSetChanged() }

                Toast.makeText(this, "Status leku zaktualizowany dla daty $dateKey", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

// POWIADOMIENIA
    /**
     * Sends a notification to the user when a new message is received.
     *
     * @param message The message details containing sender, receiver, and timestamp information.
     */
    private fun sendNotification(message: Message) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .document(message.sender)
            .get()
            .addOnSuccessListener { document ->
                val senderLogin = document.getString("login") ?: "Nieznany użytkownik"

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "default",
                        "Default Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(this, "default")
                    .setContentTitle("Nowa wiadomość od $senderLogin")
                    .setContentText(message.message)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(message.timestamp.toInt(), notification)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }

    }

    /**
     * Schedules a notification for period reminders to trigger at 8:00 PM.
     * This uses exact alarms to ensure timely delivery.
     */
@SuppressLint("ScheduleExactAlarm")
private fun schedulePeriodNotification() {
    val intent = Intent(this, periodReminder::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 20)
        set(Calendar.MINUTE,0)
    }

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}

    /**
     * Schedules a recurring notification for medicine reminders at 7:05 PM daily.
     */
private fun scheduleNotification() {
    val intent = Intent(this, MedicineReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 19)
        set(Calendar.MINUTE, 5)
    }

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}
    // FETCHE

    /**
     * Fetches the latest cycle data for the user, including last period date, cycle length, and period length.
     * Updates the UI or performs actions based on the fetched data.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchLatestCycleData() {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    val lastPeriodDateValue = userDoc.get("lastPeriodDate")
                    val cycleLength = userDoc.getLong("cycleLength")?.toInt()
                    val periodLength = userDoc.getLong("periodLength")?.toInt()

                    if (cycleLength == null || periodLength == null) {
                        showToast("Brak pełnych danych użytkownika")
                        return@addOnSuccessListener
                    }

                    val lastPeriodDate = parseCustomDate(lastPeriodDateValue)

                    if (lastPeriodDate == null) {
                        showToast("Nieprawidłowy format daty: $lastPeriodDateValue")
                        return@addOnSuccessListener
                    }
                    fetchLatestCycleDocument(lastPeriodDate, cycleLength)
                    fetchTemperatureDataAndCalculateOvulation(lastPeriodDate, cycleLength)
                } else {
                    showToast("Nie znaleziono danych użytkownika")
                }
            }
            .addOnFailureListener { e ->
                showToast("Błąd: ${e.message}")
            }
    }

    /**
     * Determines and displays the current cycle day and the days until the next period.
     * Schedules a period notification if the next period is within a day.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTodaysCycleDay() {   // to jest uzywane do przedstawiania ile dni do nastepengo okresu

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val cycleLength = userDoc.getLong("cycleLength")?.toLong()

                if (cycleLength != null) {
                    db.collection("users").document(userId).collection("cycles")
                        .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val latestCycle = querySnapshot.documents[0]
                                val lastStartDateString = latestCycle.getString("startDate")

                                if (lastStartDateString != null) {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val lastStartDate: Date = sdf.parse(lastStartDateString)!!
                                    val lastStartLocalDate = lastStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                                    val daysSinceLastStart = ChronoUnit.DAYS.between(lastStartLocalDate, selectedDate).toInt()
                                    val currentCycleDay = (daysSinceLastStart % cycleLength) + 1

                                    val nextMenstruationDate = lastStartLocalDate.plusDays(cycleLength)
                                    val daysUntilNextMenstruation = ChronoUnit.DAYS.between(selectedDate, nextMenstruationDate).toInt()
                                    Log.d("PeriodReminder", "Days until next period: $daysUntilNextMenstruation")

                                    displayCycleDay(currentCycleDay)
                                    runOnUiThread {
                                        daysLeftPeriod.text = daysUntilNextMenstruation.toString()
                                    }
                                    if (daysUntilNextMenstruation == 1) {
                                        schedulePeriodNotification()
                                    }
                                } else {
                                    Log.e("CycleDataError", "Last start date not found.")
                                }
                            } else {
                                Log.e("CycleDataError", "No cycles found.")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Failed to fetch cycles: $e")
                        }
                } else {
                    Log.e("UserDataError", "Cycle length not found for user.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to fetch user data: $e")
            }
    }

    /**
     * Fetches the status of the current period (active or ended) and updates UI elements accordingly.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchPeriodStatus() {
        db.collection("users").document(userId).collection("cycles")
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val latestCycle = documents.first()
                    if (latestCycle.contains("endDate")) {
                        updateButtonVisibility(false)
                    } else {

                        updateButtonVisibility(true)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    /**
     * Fetches the latest cycle document from Firestore and calculates or displays the next ovulation date.
     *
     * @param lastPeriodDate The date of the last period.
     * @param cycleLength The length of the user's menstrual cycle.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchLatestCycleDocument(lastPeriodDate: LocalDate, cycleLength: Int) {
        db.collection("users").document(userId).collection("cycles")
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    calculateAndDisplayDates(lastPeriodDate, cycleLength)
                    changeButtonVisibility()
                } else {
                    val latestCycle = documents.first()
                    val nextOvulationDateString = latestCycle.getString("nextOvulationDate")

                    if (nextOvulationDateString != null) {
                        val nextOvulationDate = LocalDate.parse(nextOvulationDateString)
                        displayDates(nextOvulationDate, cycleLength)
                    } else {
                        val estimatedOvulationDate = lastPeriodDate.plusDays((cycleLength - 14).toLong())
                        displayDates(estimatedOvulationDate, cycleLength)
                    }
                }
            }
            .addOnFailureListener { e ->
                showToast("Błąd: ${e.message}")
            }
    }

    /**
     * Fetches daily temperature data, identifies ovulation dates, and estimates the next ovulation.
     *
     * @param lastPeriodDate The date of the last period.
     * @param cycleLength The length of the user's menstrual cycle.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTemperatureDataAndCalculateOvulation(lastPeriodDate: LocalDate, cycleLength: Int) {
        val ovulationDays = mutableListOf<Int>()

        db.collection("users").document(userId).collection("dailyInfo")
            .get()
            .addOnSuccessListener { documents ->
                val temperatureData = mutableListOf<Pair<String, Double>>()

                for (document in documents) {
                    val date = document.id
                    val temperatureValue = document.get("temperature")
                    val temperature = when (temperatureValue) {
                        is Number -> temperatureValue.toDouble()
                        is String -> temperatureValue.toDoubleOrNull()
                        else -> null
                    }

                    if (temperature != null) {
                        temperatureData.add(Pair(date, temperature))
                    }
                }

                val groupedByMonth = temperatureData.groupBy { pair ->
                    pair.first.substring(0, 7)
                }

                var validMonthsCount = 0
                groupedByMonth.forEach { (month, data) ->
                    val daysInMonth = getDaysInMonth(month)
                    if (data.size == daysInMonth) {
                        validMonthsCount++
                        val sortedData = data.sortedBy { it.first }

                        for (i in 1 until sortedData.size) {
                            val currentTemp = sortedData[i].second
                            val previousTemp = sortedData[i - 1].second
                            val temperatureDifference = currentTemp - previousTemp

                            if (temperatureDifference in 0.2..0.7) {
                                val tempDate = LocalDate.parse(sortedData[i].first)
                                val cycleDay = calculateCycleDay(lastPeriodDate, tempDate, cycleLength)

                                if (cycleDay != null) {
                                    ovulationDays.add(cycleDay - 1)
                                    break
                                }
                            }
                        }
                    }
                }

                val estimatedOvulation = if (validMonthsCount >= 5 && ovulationDays.isNotEmpty()) {
                    val medianCycleDay = calculateMedian(ovulationDays)

                    if (medianCycleDay.toInt() in cycleLength - 14 - 2..cycleLength - 14 + 2) {
                        val cycleDay = medianCycleDay.toInt()
                        val ovulationDate = calculateDateFromCycleDay(lastPeriodDate, cycleDay, cycleLength)

                        Pair(cycleDay, ovulationDate)
                    } else {
                        val cycleDay = cycleLength - 14
                        val ovulationDate = calculateDateFromCycleDay(lastPeriodDate, cycleDay, cycleLength)

                        Pair(cycleDay, ovulationDate)
                    }
                } else {
                    val cycleDay = cycleLength - 14
                    val ovulationDate = calculateDateFromCycleDay(lastPeriodDate, cycleDay, cycleLength)

                    Pair(cycleDay, ovulationDate)
                }
                println("Owulacja przypada na dzień cyklu: ${estimatedOvulation.first}, data: ${estimatedOvulation.second}")
            }
            .addOnFailureListener { e ->
                showToast("Błąd pobierania danych: ${e.message}")
            }
    }

    /**
     * Fetches the medicine statuses for the selected date and updates the UI.
     *
     * @param selectedDate The date for which medicine statuses are fetched.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchMedicinesStatus(selectedDate: LocalDate) {

        val dailyInfoRef = db.collection("users").document(userId)
            .collection("dailyInfo").document(selectedDate.toString())
            .collection("medicines")
        Log.d("medicinesBeforeFetch dupa", medicines.toString())
        dailyInfoRef.get().addOnSuccessListener { documents ->
            if (medicines.isNotEmpty()) {
                medicines.forEach { it.isChecked = false }
                for (document in documents) {

                    val medicineId = document.id
                    val isChecked = document.getBoolean("checked") ?: false
                    Log.d("FetchedMedicineId", medicineId)
                    val medicine = medicines.find { it.id == medicineId }
                    if (medicine != null) {
                        medicine.isChecked = isChecked
                    } else {
                        Log.d("MedicineNotFound", "No medicine found with id: $medicineId")
                    }
                }
                runOnUiThread {
                    Log.d(
                        "medicinesAfterUpdate",
                        medicines.toString()
                    )
                    medicineAdapter.notifyDataSetChanged()
                }

            } else {
                Log.d("medicinesEmpty", "Medicines list is empty!")
            }

        }.addOnFailureListener { e ->
            Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("FirestoreError", "Błąd pobierania leków: ${e.message}")
        }
    }

    /**
     * Fetches the list of medicines associated with the user from Firestore.
     * Invokes `fetchMedicinesStatus` to update the statuses after fetching.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchMedicines() {
        db.collection("users").document(userId).collection("medicines")
            .get()
            .addOnSuccessListener { documents ->
                medicines.clear()
                for (document in documents) {
                    val medicineId = document.id
                    val medicineName = document.getString("medicineName") ?: "Brak nazwy"
                    val dose = document.getString("doseMedicine") ?: "Brak dawki"
                    val time = document.getString("timeMedicine") ?: "Brak czasu"
                    val isChecked = document.getBoolean("checked") ?: false

                    medicines.add(Medicine(medicineId, medicineName, isChecked, dose, time))
                }
                Log.d("FirestoreData", "Pobrano leki: $medicines")
                medicineAdapter.notifyDataSetChanged()

                // Po zakończeniu pobierania leków wywołaj fetchMedicinesStatus()
                fetchMedicinesStatus(selectedDate) // Teraz fetchMedicinesStatus() będzie wywołane po fetchMedicines
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd przy pobieraniu leków: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    /**
     * Fetches doctor visits for the selected date from Firestore and updates the UI with the data.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDoctorVisits() {
        db.collection("users").document(userId).collection("doctorVisits")
            .whereEqualTo("visitDate", selectedDate.toString()) // Dodano warunek filtrowania po dacie
            .get()
            .addOnSuccessListener { result ->
                Log.d("DoctorVisitsActivity", "Liczba dokumentów: ${result.documents.size}")
                doctors.clear()
                for (document in result) {
                    val doctor = DoctorVisit(
                        id = document.id,
                        doctorName = document.getString("doctorName") ?: "",
                        visitDate = document.getString("visitDate") ?: "",
                        time = document.getString("time") ?: "",
                        isChecked = document.getBoolean("checked") ?: false,
                        extraInfo = document.getString("extraInfo") ?: "",
                        address = document.getString("address") ?: ""
                    )
                    doctors.add(doctor)
                }
                doctorAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


// NAWIGACJA

    /**
     * Opens the calendar activity for the given user ID.
     *
     * @param userId The user's ID.
     */
    private fun openCalendarActivity(userId: String) {
        val intent = Intent(this, CalendarActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    /**
     * Opens the settings activity for the given user ID.
     *
     * @param userId The user's ID.
     */
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    /**
     * Opens the account information activity for the given user ID.
     *
     * @param userId The user's ID.
     */
    private fun openAccountWindowActivity(userId: String) {
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    /**
     * Opens the period ending activity and finishes the current one.
     *
     * @param userId The user's ID.
     */
    private fun openPeriodEnding(userId: String){
        val intent = Intent(this, PeriodEndingActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }

    /**
     * Opens the period beginning activity and finishes the current one.
     *
     * @param userId The user's ID.
     */
    private fun openPeriodBeggining(userId: String) {
        val intent = Intent(this, PeriodBegginingActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }


    /**
     * Opens the additional information activity for a given date and user ID.
     *
     * @param userId The user's ID.
     * @param date The selected date.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAdditionalInformationActivity(userId: String, date: String) {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", date)
        startActivity(intent)
    }
    data class Message(
        val message: String = "",
        val receiver: String = "",
        val sender: String = "",
        val timestamp: Long = 0L,
        val isseen: Boolean = false
    )
}
