package com.rmblack.vocabularyof12sgrade.server

import com.rmblack.vocabularyof12sgrade.server.models.URL
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface WordsApi {
    @GET("eD18RS/words_lists_urls")
    fun getURLs() : Call<ArrayList<URL>>
}