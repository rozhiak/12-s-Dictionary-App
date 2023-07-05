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
        val setayesh = Lesson("ستایش", "ملکا ذکر تو گویم", R.drawable.setayesh)
        val lessonOne = Lesson("درس اول", "شکر نعمت", R.drawable.one)
        val lessonTwo = Lesson("درس دوم", "مست و هشیار", R.drawable.two)
        val lessonThree = Lesson("درس سوم", "آزادی", R.drawable.three)
        val lessonFive = Lesson("درس پنجم", "دماوندیه", R.drawable.five)
        val lessonSix = Lesson("درس ششم", "نی نامه", R.drawable.six)
        val lessonSeven = Lesson("درس هفتم", "در حقیقت عشق", R.drawable.seven)
        val lessonEight = Lesson("درس هشتم", "از پاریز تا پاریس", R.drawable.eight)
        val lessonNine = Lesson("درس نهم", "کویر", R.drawable.nine)
        val lessonTen = Lesson("درس دهم", "فصل شکوفایی", R.drawable.ten)
        val lessonEleven = Lesson("درس یازدهم", "آن شب عزیز", R.drawable.eleven)
        val lessonTwelve = Lesson("درس دوازدهم", "گذر سیاوش از آتش", R.drawable.twelve)
        val lessonThirteen = Lesson("درس سیزدهم", "خوان هشتم", R.drawable.thirteen)
        val lessonFourteen = Lesson("درس چهاردهم", "سی مرغ و سیمرغ", R.drawable.fourteen)
        val lessonSixteen = Lesson("درس شانزدهم", "کباب غاز", R.drawable.sixteen)
        val lessonSeventeen = Lesson("درس هفدهم", "خنده تو", R.drawable.seventeen)
        val niyayesh = Lesson("نیایش ", "لطف تو", R.drawable.niyayesh)

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
            niyayesh
        )
    }
}