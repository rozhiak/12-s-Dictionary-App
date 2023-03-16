package com.rmblack.vocabularyof12sgrade.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.rmblack.vocabularyof12sgrade.R
import com.rmblack.vocabularyof12sgrade.adapter.WordAdapter
import com.rmblack.vocabularyof12sgrade.databinding.ActivityReviewWordsBinding
import com.rmblack.vocabularyof12sgrade.models.Word
import kotlin.math.abs

class ReviewWords : AppCompatActivity() {

    private lateinit var binding: ActivityReviewWordsBinding
    private lateinit var wordsViewPager: ViewPager2
    private lateinit var handler: Handler
    private lateinit var wordList: ArrayList<Word>
    private lateinit var wordsAdapter: WordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //This line should be changed
        window.navigationBarColor = resources.getColor(R.color.white)

        init()
        setUpTransformer()
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(50))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.86f + r * 0.14f
        }
        wordsViewPager.setPageTransformer(transformer)
    }

    private fun init() {
//        wordsViewPager = findViewById(R.id.wordsViewPager)
//        handler = Handler(Looper.myLooper()!!)
//        wordList = ArrayList()
//
//        val w1 = Word("لغت تست اول", "معنای اول", 0)
//        val w2 = Word("لغت تست دوم", "معنای دوم", 0)
//        val w3 = Word("لغت تست سوم", "معنای سوم", 0)
//        val w4 = Word("لغت تست چهارم", "معنای چهارم", 0)
//        val w5 = Word("لغت تست پنجم", "معنای پنجم", 0)
//        val w6 = Word("لغت تست ششم", "معنای ششم", 0)
//        val w7 = Word("لغت تست هفتم", "معنای هفتم", 0)
//        val w8 = Word("لغت تست هشتم", "معنای هشتم", 0)
//        val w9 = Word("لغت تست نهم", "معنای نهم", 0)
//        val w10 = Word("لغت تست دهم", "معنای دهم", 0)
//        val w11 = Word("لغت تست یازدم", "معنای یازدهم", 0)
//        val w12 = Word("لغت تست دوازدهم", "معنای دوازدهم", 0)
//        val w13 = Word("لغت تست سیزدهم", "معنای سیزدهم", 0)
//        val w14 = Word("لغت تست چهاردهم", "معنای چهاردهم", 0)
//        val w15 = Word("لغت تست پانزدهم", "معنای پانزدهم", 0)
//        val w16 = Word("لغت تست شانزدهم", "معنای شانزدهم", 0)
//        val w17 = Word("لغت تست هفدهم", "معنای هفدهم", 0)
//        val w18 = Word("لغت تست هجدهم", "معنای هجدهم", 0)
//        val w19 = Word("لغت تست نونزدهم", "معنای نونزدهم", 0)
//        val w20 = Word("لغت تست بیستم", "معنای بیستم", 0)
//
//
//        val words = ArrayList<Word> ()
//        words.add(w1)
//        words.add(w2)
//        words.add(w3)
//        words.add(w4)
//        words.add(w5)
//        words.add(w6)
//        words.add(w7)
//        words.add(w8)
//        words.add(w9)
//        words.add(w10)
//        words.add(w11)
//        words.add(w12)
//        words.add(w13)
//        words.add(w14)
//        words.add(w15)
//        words.add(w16)
//        words.add(w17)
//        words.add(w18)
//        words.add(w19)
//        words.add(w20)
//
//
//        wordList = words
//
//        wordsAdapter = WordAdapter(wordList)
//        wordsViewPager.adapter = wordsAdapter
//        wordsViewPager.offscreenPageLimit = 3
//        wordsViewPager.clipToPadding = false
//        wordsViewPager.clipChildren = false
//        wordsViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//
//        handleWordCardWhenSwiping(words)
    }

    private fun handleWordCardWhenSwiping(words: ArrayList<Word>) {
        wordsViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (words[position].answerVisibility) {
                    hideAnswer(position, words)
                }
                if (position + 1 < wordsAdapter.itemCount) {
                    if (words[position + 1].answerVisibility) {
                        hideAnswer(position + 1, words)
                    }
                }
            }
        })
    }

    private fun hideAnswer(position : Int, words: ArrayList<Word>) {
        val viewHolder: WordAdapter.WordVH =
            (wordsViewPager.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(
                position
            ) as WordAdapter.WordVH
        wordsAdapter.resizeWordCard(viewHolder, 80)
        wordsAdapter.changeEyeIcon(false, viewHolder)
        words[position].answerVisibility = !words[position].answerVisibility
    }

}