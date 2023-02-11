package com.rmblack.vocabularyof12sgrade.logic

data class Word(val word : String, val meaning : String, var wrongNum : Int, var answerVisibility : Boolean = false)