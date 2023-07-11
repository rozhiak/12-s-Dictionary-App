package com.rmblack.vocabularyof12sgrade.server

import com.rmblack.vocabularyof12sgrade.models.Word
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IService {
    @GET
    fun getWords(@Url url : String) : Call<ArrayList<Word>>
}