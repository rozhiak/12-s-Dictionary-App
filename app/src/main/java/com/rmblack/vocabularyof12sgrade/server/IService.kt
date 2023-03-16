package com.rmblack.vocabularyof12sgrade.server

import com.rmblack.vocabularyof12sgrade.models.Word
import com.rmblack.vocabularyof12sgrade.models.URL
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IService {
    @GET("eD18RS/words_lists_urls")
    fun getURLs() : Call<ArrayList<URL>>

    @GET
    fun getWords(@Url url : String) : Call<ArrayList<Word>>
}