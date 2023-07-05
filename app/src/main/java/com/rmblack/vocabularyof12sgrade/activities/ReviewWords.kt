package com.rmblack.vocabularyof12sgrade.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.rmblack.vocabularyof12sgrade.R
import com.rmblack.vocabularyof12sgrade.adapter.WordAdapter
import com.rmblack.vocabularyof12sgrade.databinding.ActivityReviewWordsBinding
import com.rmblack.vocabularyof12sgrade.models.Word
import com.rmblack.vocabularyof12sgrade.utils.DataBaseInfo
import com.rmblack.vocabularyof12sgrade.utils.PersianNum
import com.rmblack.vocabularyof12sgrade.viewmodels.ReviewWordsViewModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs

class ReviewWords : AppCompatActivity() {

    private lateinit var binding: ActivityReviewWordsBinding
    private lateinit var wordsViewPager: ViewPager2
    private lateinit var wordsAdapter: WordAdapter
    private val viewModel: ReviewWordsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        getIntentData()
        resetUIStatesToDefault()
        initWords(savedInstanceState)
        setUpTransformer()
        initGeneralUI()
        configBTNs()
    }

    private fun resetUIStatesToDefault() {
        for (w in viewModel.getReviewWords()) {
            w.wordState = null
            w.answerVisibility = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val serializedArray = Json.encodeToString(viewModel.getReviewWords())
        val serializedWordsArray = Json.encodeToString(viewModel.getWords())
        outState.putString("words", serializedWordsArray)
        outState.putString("words_to_review", serializedArray)
        outState.putString("num_Of_mistakes", binding.numOfMistakes.text.toString())
        outState.putString("num_Of_remaining", binding.numOfRemaining.text.toString())
        outState.putString("num_Of_studied", binding.numOfStudied.text.toString())
        val viewHolder: WordAdapter.WordVH =
            (wordsViewPager.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(
                viewModel.getCurPos()
            ) as WordAdapter.WordVH
        outState.putInt("ans_height", viewHolder.meaning.height)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.numOfMistakes.text = savedInstanceState.getString("num_Of_mistakes")
        binding.numOfRemaining.text = savedInstanceState.getString("num_Of_remaining")
        binding.numOfStudied.text = savedInstanceState.getString("num_Of_studied")
    }

    private fun configBTNs() {
        configEndBtn()
        configBackBtn()
    }

    private fun configBackBtn() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun configEndBtn() {
        binding.endBtn.setOnClickListener {
            finish()
        }
    }

    private fun initGeneralUI() {
        binding.lessonTitle.text = viewModel.getTitle()
        binding.numOfMistakes.text = PersianNum.convert("0")
        binding.numOfStudied.text = PersianNum.convert("0")
        binding.numOfRemaining.text = PersianNum.convert(wordsAdapter.itemCount.toString())
    }

    fun changeNumOfStudied(increaseOrDecrease : Boolean) {
        var curNum = binding.numOfStudied.text.toString().toInt()
        if (increaseOrDecrease) {
            curNum++
            binding.numOfStudied.text = PersianNum.convert(curNum.toString())
        } else {
            curNum--
            binding.numOfStudied.text = PersianNum.convert(curNum.toString())
        }
    }

    fun changeNumOfRemaining(increaseOrDecrease : Boolean) {
        var curNum = binding.numOfRemaining.text.toString().toInt()
        if (increaseOrDecrease) {
            curNum++
            binding.numOfRemaining.text = PersianNum.convert(curNum.toString())
        } else {
            curNum--
            binding.numOfRemaining.text = PersianNum.convert(curNum.toString())
        }
    }

    fun changeNumOfMistakes(increaseOrDecrease : Boolean) {
        var curNum = binding.numOfMistakes.text.toString().toInt()
        if (increaseOrDecrease) {
            curNum++
            binding.numOfMistakes.text = PersianNum.convert(curNum.toString())
        } else {
            curNum--
            binding.numOfMistakes.text = PersianNum.convert(curNum.toString())
        }
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

    private fun initWords(savedInstanceState: Bundle?) {
        initViewPager(savedInstanceState)
        binding.wordsNum.text = PersianNum.convert(viewModel.getReviewWords().size.toString())
    }

    private fun initViewPager(savedInstanceState: Bundle?) {
        wordsViewPager = findViewById(R.id.wordsViewPager)
        if (savedInstanceState != null) {
            viewModel.updateWords(savedInstanceState.getString("words")
                ?.let { Json.decodeFromString(it) }!!)
            viewModel.updateWordsToReview(savedInstanceState.getString("words_to_review")
                ?.let { Json.decodeFromString(it) }!!)
        }
        wordsAdapter = WordAdapter(viewModel.getReviewWords(), this, savedInstanceState)
        wordsViewPager.adapter = wordsAdapter
        wordsViewPager.offscreenPageLimit = 3
        wordsViewPager.clipToPadding = false
        wordsViewPager.clipChildren = false
        wordsViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        handleWordCardWhenSwiping(viewModel.getReviewWords())
    }

    private fun getIntentData() {
        val serializedDataPack = intent.getStringExtra(DataBaseInfo.BUNDLE_REVIEW_DATA_PACK).toString()
        viewModel.initializeData(serializedDataPack, this)
    }

    private fun handleWordCardWhenSwiping(words: ArrayList<Word>) {
        wordsViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                viewModel.updateCurPos(position)
                if (wordsViewPager.scrollState != 0) {
                    binding.wordsViewPager.post {
                        wordsAdapter.setIconsWhenSwiping(position-1)
                    }
                    hidePreviousWordAnswer(words, position)
                }
            }
        })
    }

    private fun hidePreviousWordAnswer(
        words: ArrayList<Word>,
        position: Int
    ) {
        if (words[position].answerVisibility) {
            hideAnswer(position, words)
        }
        if (position + 1 < wordsAdapter.itemCount) {
            if (words[position + 1].answerVisibility) {
                hideAnswer(position + 1, words)
            }
        }
    }

    private fun hideAnswer(position : Int, words: ArrayList<Word>) {
        val viewHolder: WordAdapter.WordVH =
            (wordsViewPager.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(
                position
            ) as WordAdapter.WordVH
        wordsAdapter.resizeWordCard(viewHolder, 80, 190)
        wordsAdapter.changeEyeIcon(false, viewHolder)
        words[position].answerVisibility = !words[position].answerVisibility
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveResToDB(this)
    }
}