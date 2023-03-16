package com.rmblack.vocabularyof12sgrade.models

data class Word(val id : String, val word : String, val meaning : String, var wrongNum : Int, var answerVisibility : Boolean = false)