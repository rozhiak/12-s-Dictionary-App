package com.rmblack.vocabularyof12sgrade.models

data class Lesson(val number: String, val title: String, val lessonImage: Int, var visibility: Boolean = false) {

    var words : ArrayList<Word>? = null

}