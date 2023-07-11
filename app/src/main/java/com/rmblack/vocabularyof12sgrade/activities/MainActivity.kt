package com.rmblack.vocabularyof12sgrade.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rmblack.vocabularyof12sgrade.R
import com.rmblack.vocabularyof12sgrade.adapter.LessonsRecAdapter
import com.rmblack.vocabularyof12sgrade.databinding.ActivityMainBinding
import com.rmblack.vocabularyof12sgrade.viewmodels.MainViewModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.teeth_white)
        initializeLessons(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("lessons", Json.encodeToString(mainViewModel.getLessons()))
    }

    private fun initializeLessons(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val serializedLessons = savedInstanceState.getString("lessons")
            mainViewModel.updateLessons(Json.decodeFromString(serializedLessons.toString()))
        }
        configLessonsRec()
    }

    private fun configLessonsRec() {
        val rvLessons = findViewById<View>(R.id.rvLessons) as RecyclerView
        val adapter = LessonsRecAdapter(mainViewModel.getLessons(), this, binding)
        rvLessons.adapter = adapter
        rvLessons.layoutManager = LinearLayoutManager(this)
    }
}