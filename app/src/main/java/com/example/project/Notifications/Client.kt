package com.example.project.Notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Singleton object responsible for creating and providing a Retrofit client instance.
 * Ensures that only one instance of Retrofit is created and reused across the application.
 */
object Client {

    private var retrofit: Retrofit? = null

    /**
     * Returns a Retrofit client instance for the given base URL.
     * Uses the Double-Checked Locking pattern to ensure thread safety and efficient instance creation.
     *
     * @param url The base URL for the Retrofit client.
     * @return A configured Retrofit instance.
     */
    fun getClient(url: String): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofit = it }
        }
    }
}