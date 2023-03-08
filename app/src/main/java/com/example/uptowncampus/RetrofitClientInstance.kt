package com.example.uptowncampus

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientInstance {
    private var retrofit: Retrofit? = null
    private const val BASE_URL = "https://homepages.uc.edu/"

    val retrofitInstance : Retrofit?
        get() {
            // check if this object has been created
            if (retrofit == null) {
                // create it
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}