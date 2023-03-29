package com.rmblack.vocabularyof12sgrade.models

import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class Word(val id : String, val word : String, val meaning : String, var wrongNum : Int, @Transient var answerVisibility : Boolean = false, @Transient var wordState: Boolean? = null) {
    fun increaseWrongNum() {
        wrongNum++
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