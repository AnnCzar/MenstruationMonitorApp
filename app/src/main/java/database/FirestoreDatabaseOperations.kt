package database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.collections.Cycles
import database.collections.DailyInfo
import database.collections.DoctorVisits
import database.collections.Medications
import database.collections.Pregnancy
import database.collections.Users
import kotlinx.coroutines.tasks.await

class FirestoreDatabaseOperations(private val db: FirebaseFirestore = Firebase.firestore) : FirestoreInterface {

    // Metody dla kolekcji Users
    override suspend fun addUser(userId: String, user: Users) {
        try {
            db.collection("users").document(userId).set(user).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas dodawania użytkownika do bazy danych: ${e.message}")
        }
    }

    override suspend fun getUser(userId: String): Users? {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            doc.toObject(Users::class.java)
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas pobierania użytkownika z bazy danych: ${e.message}")
            null
        }
    }

    override suspend fun updateUser(userId: String, updatedUser: Users) {
        try {
            db.collection("users").document(userId).set(updatedUser, SetOptions.merge()).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas aktualizacji użytkownika w bazie danych: ${e.message}")
        }
    }

    override suspend fun deleteUser(userId: String) {
        try {
            db.collection("users").document(userId).delete().await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas usuwania użytkownika z bazy danych: ${e.message}")
        }
    }

    // Metody dla kolekcji Pregnancy
    override suspend fun addPregnancy(userId: String, pregnancy: Pregnancy) {
        try {
            db.collection("pregnancy").document(userId).set(pregnancy).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas dodawania ciąży do bazy danych: ${e.message}")
        }
    }

    override suspend fun getPregnancy(userId: String): Pregnancy? {
        return try {
            val doc = db.collection("pregnancy").document(userId).get().await()
            doc.toObject(Pregnancy::class.java)
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas pobierania ciąży z bazy danych: ${e.message}")
            null
        }
    }

    override suspend fun updatePregnancy(userId: String, updatedPregnancy: Pregnancy) {
        try {
            db.collection("pregnancy").document(userId).set(updatedPregnancy, SetOptions.merge()).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas aktualizacji ciąży w bazie danych: ${e.message}")
        }
    }

    override suspend fun deletePregnancy(userId: String) {
        try {
            db.collection("pregnancy").document(userId).delete().await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas usuwania ciąży z bazy danych: ${e.message}")
        }
    }

    // Metody dla kolekcji Medication
    override suspend fun addMedication(userId: String, medication: Medications) {
        try {
            db.collection("medication").document(userId).set(medication).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas dodawania leku do bazy danych: ${e.message}")
        }
    }

    override suspend fun getMedication(userId: String): Medications? {
        return try {
            val doc = db.collection("medication").document(userId).get().await()
            doc.toObject(Medications::class.java)
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas pobierania leku z bazy danych: ${e.message}")
            null
        }
    }

    override suspend fun updateMedication(userId: String, updatedMedication: Medications) {
        try {
            db.collection("medication").document(userId).set(updatedMedication, SetOptions.merge()).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas aktualizacji leku w bazie danych: ${e.message}")
        }
    }

    override suspend fun deleteMedication(userId: String) {
        try {
            db.collection("medication").document(userId).delete().await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas usuwania leku z bazy danych: ${e.message}")
        }
    }

    // Metody dla kolekcji DoctorVisit
    override suspend fun addDoctorVisit(userId: String, doctorVisit: DoctorVisits) {
        try {
            db.collection("doctorVisit").document(userId).set(doctorVisit).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas dodawania wizyty lekarskiej do bazy danych: ${e.message}")
        }
    }

    override suspend fun getDoctorVisit(userId: String): DoctorVisits? {
        return try {
            val doc = db.collection("doctorVisit").document(userId).get().await()
            doc.toObject(DoctorVisits::class.java)
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas pobierania wizyty lekarskiej z bazy danych: ${e.message}")
            null
        }
    }

    override suspend fun updateDoctorVisit(userId: String, updatedDoctorVisit: DoctorVisits) {
        try {
            db.collection("doctorVisit").document(userId).set(updatedDoctorVisit, SetOptions.merge()).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas aktualizacji wizyty lekarskiej w bazie danych: ${e.message}")
        }
    }

    override suspend fun deleteDoctorVisit(userId: String) {
        try {
            db.collection("doctorVisit").document(userId).delete().await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas usuwania wizyty lekarskiej z bazy danych: ${e.message}")
        }
    }

    // Metody dla kolekcji DailyInfo
    override suspend fun addDailyInfo(userId: String, dailyInfo: DailyInfo) {
        try {
            db.collection("dailyInfo").document(userId).set(dailyInfo).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas dodawania danych dziennych do bazy danych: ${e.message}")
        }
    }

    override suspend fun getDailyInfo(userId: String): DailyInfo? {
        return try {
            val doc = db.collection("dailyInfo").document(userId).get().await()
            doc.toObject(DailyInfo::class.java)
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas pobierania danych dziennych z bazy danych: ${e.message}")
            null
        }
    }

    override suspend fun updateDailyInfo(userId: String, updatedDailyInfo: DailyInfo) {
        try {
            db.collection("dailyInfo").document(userId).set(updatedDailyInfo, SetOptions.merge()).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas aktualizacji danych dziennych w bazie danych: ${e.message}")
        }
    }

    override suspend fun deleteDailyInfo(userId: String) {
        try {
            db.collection("dailyInfo").document(userId).delete().await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas usuwania danych dziennych z bazy danych: ${e.message}")
        }
    }

    // Metody dla kolekcji Cycle
    override suspend fun addCycle(userId: String, cycle: Cycles) {
        try {
            db.collection("cycle").document(userId).set(cycle).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas dodawania cyklu do bazy danych: ${e.message}")
        }
    }

    override suspend fun getCycle(userId: String): Cycles? {
        return try {
            val doc = db.collection("cycle").document(userId).get().await()
            doc.toObject(Cycles::class.java)
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas pobierania cyklu z bazy danych: ${e.message}")
            null
        }
    }

    override suspend fun updateCycle(userId: String, updatedCycle: Cycles) {
        try {
            db.collection("cycle").document(userId).set(updatedCycle, SetOptions.merge()).await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas aktualizacji cyklu w bazie danych: ${e.message}")
        }
    }

    override suspend fun deleteCycle(userId: String) {
        try {
            db.collection("cycle").document(userId).delete().await()
        } catch (e: Exception) {
            // Obsługa błędów
            println("Błąd podczas usuwania cyklu z bazy danych: ${e.message}")
        }
    }
}
