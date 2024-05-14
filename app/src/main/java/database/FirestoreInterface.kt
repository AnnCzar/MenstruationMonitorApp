package database

import database.collections.*

interface FirestoreInterface {

    // Metody dla kolekcji Users
    suspend fun addUser(userId: String, user: Users)
    suspend fun getUser(userId: String): Users?
    suspend fun updateUser(userId: String, updatedUser: Users)
    suspend fun deleteUser(userId: String)

    // Metody dla kolekcji Pregnancy
    suspend fun addPregnancy(userId: String, pregnancy: Pregnancy)
    suspend fun getPregnancy(userId: String): Pregnancy?
    suspend fun updatePregnancy(userId: String, updatedPregnancy: Pregnancy)
    suspend fun deletePregnancy(userId: String)

    // Metody dla kolekcji Medication
    suspend fun addMedication(userId: String, medication: Medications)
    suspend fun getMedication(userId: String): Medications?
    suspend fun updateMedication(userId: String, updatedMedication: Medications)
    suspend fun deleteMedication(userId: String)

    // Metody dla kolekcji DoctorVisit
    suspend fun addDoctorVisit(userId: String, doctorVisit: DoctorVisits)
    suspend fun getDoctorVisit(userId: String): DoctorVisits?
    suspend fun updateDoctorVisit(userId: String, updatedDoctorVisit: DoctorVisits)
    suspend fun deleteDoctorVisit(userId: String)

    // Metody dla kolekcji DailyInfo
    suspend fun addDailyInfo(userId: String, dailyInfo: DailyInfo)
    suspend fun getDailyInfo(userId: String): DailyInfo?
    suspend fun updateDailyInfo(userId: String, updatedDailyInfo: DailyInfo)
    suspend fun deleteDailyInfo(userId: String)

    // Metody dla kolekcji Cycle
    suspend fun addCycle(userId: String, cycle: Cycles)
    suspend fun getCycle(userId: String): Cycles?
    suspend fun updateCycle(userId: String, updatedCycle: Cycles)
    suspend fun deleteCycle(userId: String)
}
