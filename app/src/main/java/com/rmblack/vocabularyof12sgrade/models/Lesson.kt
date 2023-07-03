package com.rmblack.vocabularyof12sgrade.models

@kotlinx.serialization.Serializable
data class Lesson(val number: String,
                  val title: String,
                  val lessonImage: Int,
                  var visibility: Boolean = false,
                  var firstSwitch: Boolean = false,
                  var secondSwitch: Boolean  = false,
                  var thirdSwitch: Boolean  = false,
                  var forthSwitch: Boolean  = false)