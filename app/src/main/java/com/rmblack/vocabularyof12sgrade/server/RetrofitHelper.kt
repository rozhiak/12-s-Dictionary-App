package com.rmblack.vocabularyof12sgrade.server

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val baseURL = "https://api-generator.retool.com/"

    fun getInstance() : Retrofit {
        return Retrofit.Builder().baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}