package com.rmblack.vocabularyof12sgrade.viewmodels

import androidx.lifecycle.ViewModel
import com.rmblack.vocabularyof12sgrade.R
import com.rmblack.vocabularyof12sgrade.models.Lesson

class MainViewModel : ViewModel() {

    private var lessons : ArrayList<Lesson> = ArrayList()

    init {
        initializeLessons()
    }

    fun getLessons(): ArrayList<Lesson> {
        return lessons
    }

    fun updateLessons(lessons: ArrayList<Lesson>) {
        this.lessons = lessons
    }

    private fun initializeLessons() {
        //Declare lessons
        val setayesh = Lesson("ستایش", "ملکا ذکر تو گویم", R.drawable.setayesh, "SjHkFz/setayesh_grade12")
        val lessonOne = Lesson("درس اول", "شکر نعمت", R.drawable.one, "6xFgSS/lesson1")
        val lessonTwo = Lesson("درس دوم", "مست و هشیار", R.drawable.two, "SX4djI/lesson2_grade12")
        val lessonThree = Lesson("درس سوم", "آزادی", R.drawable.three, "GaszQw/lesson3_grade12")
        val lessonFive = Lesson("درس پنجم", "دماوندیه", R.drawable.five, "SzKtoy/lesson5_grade12")
        val lessonSix = Lesson("درس ششم", "نی نامه", R.drawable.six, "r54gaH/lesson6_grade12")
        val lessonSeven = Lesson("درس هفتم", "در حقیقت عشق", R.drawable.seven, "QUeci1/lesson7_grade12")
        val lessonEight = Lesson("درس هشتم", "از پاریز تا پاریس", R.drawable.eight, "l7l8gG/lesson8_grade12")
        val lessonNine = Lesson("درس نهم", "کویر", R.drawable.nine, "8AcvSS/lesson9_grade12")
        val lessonTen = Lesson("درس دهم", "فصل شکوفایی", R.drawable.ten, "FjI9VZ/lesson10_grade12")
        val lessonEleven = Lesson("درس یازدهم", "آن شب عزیز", R.drawable.eleven, "8vctnF/lesson11_grade12")
        val lessonTwelve = Lesson("درس دوازدهم", "گذر سیاوش از آتش", R.drawable.twelve, "TnpcwI/lesson12_grade12")
        val lessonThirteen = Lesson("درس سیزدهم", "خوان هشتم", R.drawable.thirteen, "JZLnfK/lesson13_grade12")
        val lessonFourteen = Lesson("درس چهاردهم", "سی مرغ و سیمرغ", R.drawable.fourteen, "OkMsEc/lesson14_grade12")
        val lessonSixteen = Lesson("درس شانزدهم", "کباب غاز", R.drawable.sixteen, "k4BGOl/lesson16_grade12")
        val lessonSeventeen = Lesson("درس هفدهم", "خنده تو", R.drawable.seventeen, "PRkcPI/lesson17_grade12")
        val lessonEighteen = Lesson("درس هجدهم", "عشق جاودانی", R.drawable.eighteen, "w8I6xS/lesson18_grade12")
        val niyayesh = Lesson("نیایش ", "لطف تو", R.drawable.niyayesh, "ScFxH3/niyayesh")

        lessons = arrayListOf(
            setayesh,
            lessonOne,
            lessonTwo,
            lessonThree,
            lessonFive,
            lessonSix,
            lessonSeven,
            lessonEight,
            lessonNine,
            lessonTen,
            lessonEleven,
            lessonTwelve,
            lessonThirteen,
            lessonFourteen,
            lessonSixteen,
            lessonSeventeen,
            lessonEighteen,
            niyayesh
        )
    }
}