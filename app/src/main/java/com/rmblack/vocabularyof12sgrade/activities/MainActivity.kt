package com.rmblack.vocabularyof12sgrade.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rmblack.vocabularyof12sgrade.R
import com.rmblack.vocabularyof12sgrade.adapter.LessonsRecAdapter
import com.rmblack.vocabularyof12sgrade.databinding.ActivityMainBinding
import com.rmblack.vocabularyof12sgrade.models.Lesson
import com.rmblack.vocabularyof12sgrade.utils.DataBaseInfo

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var lessons: ArrayList<Lesson>

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.teeth_white)
        initializeLessons()
//        getSharedPreferences(DataBaseInfo.SP_NAME, Context.MODE_PRIVATE).edit().clear().commit()
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

        configLessonsRec(lessons)
    }

    private fun configLessonsRec(lessons: List<Lesson>) {
        val rvLessons = findViewById<View>(R.id.rvLessons) as RecyclerView
        val adapter = LessonsRecAdapter(lessons, this, binding)
        rvLessons.adapter = adapter
        rvLessons.layoutManager = LinearLayoutManager(this)

    }

    override fun onResume() {
        super.onResume()
        configLessonsRec(lessons)
    }

}