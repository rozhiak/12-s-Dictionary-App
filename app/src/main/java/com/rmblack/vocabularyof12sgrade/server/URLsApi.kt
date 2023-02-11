package com.rmblack.vocabularyof12sgrade.server

import com.rmblack.vocabularyof12sgrade.server.models.URL
import retrofit2.Response
import retrofit2.http.GET

interface URLsApi {
    @GET("words_lists_urls")
    suspend fun getURLs() : Response<ArrayList<URL>>
}