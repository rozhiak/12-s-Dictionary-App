package com.rmblack.vocabularyof12sgrade.models

@kotlinx.serialization.Serializable
data class Lesson(val number: String, val title: String, val lessonImage: Int, var visibility: Boolean = false) {
    var words : ArrayList<Word>? = null
    var wordsToReview : ArrayList<Word>? = null

    fun increaseWordWrongNum(word: Word) {
        val index = indexOf(word)
        words?.get(index)?.increaseWrongNum()
    }

    fun increaseWordCorrectNum(word: Word) {
        val index = indexOf(word)
        words?.get(index)?.increaseCorrectNum()
    }

    private fun indexOf(word: Word): Int {
        for (w in words!!) {
            if(w.isEqual(word)) {
                return words!!.indexOf(w)
            }
        }
        return -1
    }
}