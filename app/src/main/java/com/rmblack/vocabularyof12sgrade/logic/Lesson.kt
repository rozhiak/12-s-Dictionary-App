package com.rmblack.vocabularyof12sgrade.logic

import android.graphics.drawable.Drawable

data class Lesson(val number: String, val title: String, val lessonImage: Int, var visibility: Boolean = false) {

    var words : ArrayList<Word>? = null

}