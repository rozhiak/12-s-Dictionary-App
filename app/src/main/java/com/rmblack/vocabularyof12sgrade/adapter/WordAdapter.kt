package com.rmblack.vocabularyof12sgrade.adapter

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rmblack.vocabularyof12sgrade.R
import com.rmblack.vocabularyof12sgrade.activities.ReviewWords
import com.rmblack.vocabularyof12sgrade.models.Word

class WordAdapter(private val wordList : ArrayList<Word>,
                  private val reviewWords: ReviewWords,
                  private val savedStateInstances: Bundle?)
    : RecyclerView.Adapter<WordAdapter.WordVH>() {

    private lateinit var holder: WordVH

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_row, parent, false)
        return WordVH(view)
    }

    override fun onBindViewHolder(holder: WordVH, position: Int) {
        this.holder = holder
        init(holder, position)
        val word : Word = wordList[position]
        showAnswer(holder, word)
        configCheckAndQButton(holder, position)
        setIcons(position, holder)
        resetWordCardHeightToDefault(savedStateInstances, position, holder)
    }

    private fun resetWordCardHeightToDefault(
        savedStateInstances: Bundle?,
        position: Int,
        holder: WordVH
    ) {
        if (savedStateInstances != null) {
            val curAsnHeight = savedStateInstances.getInt("ans_height")
            if (wordList[position].answerVisibility) {
                val params = holder.wordCard.layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin = curAsnHeight + 110
                holder.wordCard.layoutParams = params
            }
        }
    }

    private fun setIcons(
        position: Int,
        holder: WordVH
    ) {
        if (wordList[position].answerVisibility) {
            holder.showAnswerImg.setImageResource(R.drawable.open_eye)
            holder.showAnswerImg.setBackgroundResource(R.color.purple)
        } else {
            holder.showAnswerImg.setImageResource(R.drawable.close_eye_icon)
            holder.showAnswerImg.setBackgroundResource(R.color.white)
        }
        if (wordList[position].wordState == null) {
            holder.checkImg.setImageResource(R.drawable.check_icon)
            holder.questionImg.setImageResource(R.drawable.cross_icon)
        } else if (wordList[position].wordState == true) {
            holder.checkImg.setImageResource(R.drawable.green_check_logo)
            holder.questionImg.setImageResource(R.drawable.cross_icon)

        } else if (wordList[position].wordState != true) {
            holder.checkImg.setImageResource(R.drawable.check_icon)
            holder.questionImg.setImageResource(R.drawable.orange_cross_icon)
        }
    }

    fun setIconsWhenSwiping(prePos: Int) {
        notifyItemChanged(prePos)
        notifyItemChanged(prePos + 2)
    }

    private fun configCheckAndQButton(holder: WordVH, position: Int) {
        checkCardClick(holder, position)
        questionCardClick(holder, position)
    }

    private fun questionCardClick(
        holder: WordVH,
        position: Int
    ) {
        holder.questionCard.setOnClickListener {
            if (wordList[position].wordState == null) {
                wordList[position].increaseWrongNum()
                setQuestionIconWhenNull(holder, position)
            } else if (wordList[position].wordState == true) {
                wordList[position].decreaseCorrectNum()
                wordList[position].increaseWrongNum()
                setQuestionIconWhenTrue(position, holder)
            } else if (wordList[position].wordState != true) {
                wordList[position].decreaseWrongNum()
                setQuestionIconWhenFalse(position, holder)
            }
        }
    }

    private fun setQuestionIconWhenFalse(
        position: Int,
        holder: WordVH
    ) {
        wordList[position].wordState = null
        holder.questionImg.setImageResource(R.drawable.cross_icon)
        reviewWords.changeNumOfMistakes(false)
        reviewWords.changeNumOfRemaining(true)
    }

    private fun setQuestionIconWhenTrue(
        position: Int,
        holder: WordVH
    ) {
        wordList[position].wordState = !wordList[position].wordState!!
        holder.questionImg.setImageResource(R.drawable.orange_cross_icon)
        holder.checkImg.setImageResource(R.drawable.check_icon)
        reviewWords.changeNumOfMistakes(true)
        reviewWords.changeNumOfStudied(false)
    }

    private fun setQuestionIconWhenNull(
        holder: WordVH,
        position: Int
    ) {
        holder.questionImg.setImageResource(R.drawable.orange_cross_icon)
        wordList[position].wordState = false
        reviewWords.changeNumOfMistakes(true)
        reviewWords.changeNumOfRemaining(false)
        if (position < wordList.size - 1) {
            recyclerView.post {
                recyclerView.smoothScrollToPosition(position + 1)
            }
            if (wordList[position].answerVisibility) {
                hide(holder)
            }
        }
    }

    private fun checkCardClick(
        holder: WordVH,
        position: Int
    ) {
        holder.checkCard.setOnClickListener {
            if (wordList[position].wordState == null) {
                wordList[position].increaseCorrectNum()
                setCheckIconWhenNull(holder, position)
            } else if (wordList[position].wordState == true) {
                wordList[position].decreaseCorrectNum()
                setCheckIconWhenTrue(position, holder)
            } else if (wordList[position].wordState != true) {
                wordList[position].increaseCorrectNum()
                wordList[position].decreaseWrongNum()
                setCheckIconWhenFalse(position, holder)
            }
        }
    }

    private fun setCheckIconWhenFalse(
        position: Int,
        holder: WordVH
    ) {
        wordList[position].wordState = !wordList[position].wordState!!
        holder.checkImg.setImageResource(R.drawable.green_check_logo)
        holder.questionImg.setImageResource(R.drawable.cross_icon)
        reviewWords.changeNumOfMistakes(false)
        reviewWords.changeNumOfStudied(true)
    }

    private fun setCheckIconWhenTrue(
        position: Int,
        holder: WordVH
    ) {
        wordList[position].wordState = null
        holder.checkImg.setImageResource(R.drawable.check_icon)
        reviewWords.changeNumOfStudied(false)
        reviewWords.changeNumOfRemaining(true)
    }

    private fun setCheckIconWhenNull(
        holder: WordVH,
        position: Int
    ) {
        holder.checkImg.setImageResource(R.drawable.green_check_logo)
        wordList[position].wordState = true
        reviewWords.changeNumOfStudied(true)
        reviewWords.changeNumOfRemaining(false)
        if (position < wordList.size - 1) {
            recyclerView.post {
                recyclerView.smoothScrollToPosition(position + 1)
            }
            if (wordList[position].answerVisibility) {
                hide(holder)
            }
        }
    }

    private fun showAnswer(
        holder: WordVH,
        word: Word
    ) {
        holder.showAnswerCard.setOnClickListener {
            word.answerVisibility = !word.answerVisibility
            if (word.answerVisibility) {
                show(holder)
            } else {
                hide(holder)
            }
        }
        holder.answerCard.setOnClickListener {
            word.answerVisibility = !word.answerVisibility
            if (word.answerVisibility) {
                show(holder)
            } else {
                hide(holder)
            }
        }
    }

    private fun hide(holder: WordVH) {
        resizeWordCard(holder, 80, 190)
        changeEyeIcon(false, holder)
    }

    private fun show(holder: WordVH) {
        val size = holder.meaning.measuredHeight
        resizeWordCard(holder, size + 110, 190)
        changeEyeIcon(true, holder)
    }

    private fun init(
        holder: WordVH,
        position: Int
    ) {
        holder.word.text = wordList[position].word
        holder.meaning.text = wordList[position].meaning
        holder.meaning.bringToFront()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            holder.meaning.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    fun changeEyeIcon(open: Boolean, holder: WordVH) {
        if (open) {
            holder.showAnswerImg.setImageResource(R.drawable.open_eye)
            holder.showAnswerImg.setBackgroundResource(R.color.purple)
        } else {
            holder.showAnswerImg.setImageResource(R.drawable.close_eye_icon)
            holder.showAnswerImg.setBackgroundResource(R.color.white)
        }
    }

    fun resizeWordCard(holder: WordVH, size: Int, duration: Long) {
        val lParams = holder.wordCard.layoutParams as ViewGroup.MarginLayoutParams
        val bottomMarginStart = lParams.bottomMargin // your start value
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val params = holder.wordCard.layoutParams as ViewGroup.MarginLayoutParams
                // interpolate the proper value
                params.bottomMargin = bottomMarginStart + ((size - bottomMarginStart) * interpolatedTime).toInt()
                holder.wordCard.layoutParams = params
            }
        }
        a.duration = duration
        holder.wordCard.startAnimation(a)
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    class WordVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val word : AppCompatTextView = itemView.findViewById(R.id.word_tv)
        val wordCard : CardView = itemView.findViewById(R.id.word_card)
        val meaning : AppCompatTextView = itemView.findViewById(R.id.meaning_tv)
        val showAnswerCard : CardView = itemView.findViewById(R.id.show_answer_card)
        val showAnswerImg : AppCompatImageView = itemView.findViewById(R.id.show_answer_img)
        val checkImg : AppCompatImageView = itemView.findViewById(R.id.check_img)
        val questionImg : AppCompatImageView = itemView.findViewById(R.id.cross_img)
        val checkCard : CardView = itemView.findViewById(R.id.check_card)
        val questionCard : CardView = itemView.findViewById(R.id.question_card)
        val answerCard : CardView = itemView.findViewById(R.id.answer_card)

    }
}