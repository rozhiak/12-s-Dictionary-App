package com.rmblack.vocabularyof12sgrade.models

@kotlinx.serialization.Serializable
data class Word(val id : String, val word : String, val meaning : String, var wrongNum : Int, var correctNum : Int, var answerVisibility : Boolean = false, var wordState: Boolean? = null) {

    fun increaseCorrectNum() {
        correctNum++
    }

    fun increaseWrongNum() {
        wrongNum++
    }

    fun decreaseCorrectNum() {
        correctNum--
    }

    fun decreaseWrongNum() {
        wrongNum--
    }

    fun isEqual(w1: Word): Boolean {
        if (w1.word != this.word) {
            return false
        } else if (w1.meaning != this.meaning) {
            return false
        }
        return true
    }
}