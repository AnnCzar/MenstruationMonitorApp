package com.example.project.Notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {

    private var retrofit: Retrofit? = null

    fun getClient(url: String): Retrofit {
        // Użycie podwójnego sprawdzania (Double-Checked Locking) dla większej wydajności
        return retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofit = it }
        }
    }
}