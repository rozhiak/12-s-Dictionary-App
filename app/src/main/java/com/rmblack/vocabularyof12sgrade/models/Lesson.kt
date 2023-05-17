package com.rmblack.vocabularyof12sgrade.models

@kotlinx.serialization.Serializable
data class Lesson(val number: String,
                  val title: String,
                  val lessonImage: Int,
                  var visibility: Boolean = false,
                  var firstSwitch: Boolean = false,
                  var secondSwitch: Boolean  = false,
                  var thirdSwitch: Boolean  = false,
                  var forthSwitch: Boolean  = false) {
    var words : ArrayList<Word>? = null
    var wordsToReview : ArrayList<Word>? = null

    fun indexOf(word: Word): Int {
        for (w in words!!) {
            if(w.isEqual(word)) {
                return words!!.indexOf(w)
            }
        }
        return -1
    }
}