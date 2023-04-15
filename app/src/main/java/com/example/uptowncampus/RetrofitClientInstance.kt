package com.example.uptowncampus

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object responsible for creating a single instance of the Retrofit client.
 * It provides the base URL for the API to access the JSON data and converts it to a POJO using Gson.
 */
object RetrofitClientInstance {
    private var retrofit: Retrofit? = null
    private const val BASE_URL = "https://homepages.uc.edu/"

    /**
     * Returns the instance of the Retrofit client. If the client hasn't been created yet,
     * it will be created using the BASE_URL and GsonConverterFactory, and then returned.
     * @return the instance of the Retrofit client
     */
    val retrofitInstance : Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}