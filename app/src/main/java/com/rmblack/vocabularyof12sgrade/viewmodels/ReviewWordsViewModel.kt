package com.rmblack.vocabularyof12sgrade.viewmodels

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.rmblack.vocabularyof12sgrade.activities.ReviewWords
import com.rmblack.vocabularyof12sgrade.models.ReviewIntentDataPack
import com.rmblack.vocabularyof12sgrade.models.ReviewType
import com.rmblack.vocabularyof12sgrade.models.Word
import com.rmblack.vocabularyof12sgrade.utils.DataBaseInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReviewWordsViewModel : ViewModel() {

    private lateinit var words: ArrayList<Word>
    private lateinit var wordsToReview: ArrayList<Word>
    private lateinit var title: String
    private var curPos: Int = 0

    fun getCurPos(): Int {
        return curPos
    }

    fun updateCurPos(curPos: Int) {
        this.curPos= curPos
    }

    fun getWords() : ArrayList<Word> {
        return words
    }

    fun getReviewWords() : ArrayList<Word> {
        return wordsToReview
    }

    fun updateWordsToReview(wordsToReview: ArrayList<Word>) {
        this.wordsToReview = wordsToReview
    }

    fun updateWords(words: ArrayList<Word>) {
        this.words = words
    }

    fun getTitle(): String {
        return title
    }

    fun initializeData(serializedDataPack: String, reviewWords: ReviewWords) {
        val dataPack: ReviewIntentDataPack = Json.decodeFromString(serializedDataPack)
        title = dataPack.title
        words = getWordsFromDB(dataPack.title, reviewWords)
        if (dataPack.reviewType == ReviewType.REVIEW_ALL) {
            wordsToReview = words
        } else {
            wordsToReview = ArrayList()
            collectWords(words, dataPack.oneM!!, dataPack.twoM!!, dataPack.threeM!!, dataPack.moreM!!)
        }
    }

    private fun getWordsFromDB(title: String,
                               reviewWords: ReviewWords): java.util.ArrayList<Word> {
        val sp = reviewWords.getSharedPreferences(DataBaseInfo.SP_NAME, Context.MODE_PRIVATE)
        return Json.decodeFromString(
            sp.getString(title, "").toString()
        )
    }

    fun saveResToDB(reviewWords: ReviewWords) {
        val sp = reviewWords.getSharedPreferences(DataBaseInfo.SP_NAME, AppCompatActivity.MODE_PRIVATE)
        val editor = sp.edit()
        val serializedArray = Json.encodeToString(words)
        editor.putString(title, serializedArray)
        editor.apply()
    }

    private fun collectWords(
        words: java.util.ArrayList<Word>,
        oneMis: Boolean,
        twoMis: Boolean,
        threeMis: Boolean,
        threeAndMoreMis: Boolean
    ) {
        for (w in words) {
            if (oneMis && w.wrongNum == 1) {
                wordsToReview.add(w)
            }
            if (twoMis && w.wrongNum == 2) {
                wordsToReview.add(w)
            }
            if (threeMis && w.wrongNum == 3) {
                wordsToReview.add(w)
            }
            if (threeAndMoreMis && w.wrongNum > 3) {
                wordsToReview.add(w)
            }
        }
    }
}