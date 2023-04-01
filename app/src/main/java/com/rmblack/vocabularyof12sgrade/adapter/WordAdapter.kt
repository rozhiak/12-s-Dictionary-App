package com.rmblack.vocabularyof12sgrade.adapter

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

class WordAdapter(private val wordList : ArrayList<Word>, private val reviewWords: ReviewWords)
    : RecyclerView.Adapter<WordAdapter.WordVH>() {

    private lateinit var holder: WordVH

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
    }

    private fun setIcons(
        position: Int,
        holder: WordVH
    ) {
        if (wordList[position].wordState == null) {
            holder.checkImg.setImageResource(R.drawable.check_icon)
            holder.questionImg.setImageResource(R.drawable.question_icon)
        } else if (wordList[position].wordState == true) {
            holder.checkImg.setImageResource(R.drawable.green_check_logo)
            holder.questionImg.setImageResource(R.drawable.question_icon)

        } else if (wordList[position].wordState != true) {
            holder.checkImg.setImageResource(R.drawable.check_icon)
            holder.questionImg.setImageResource(R.drawable.orange_question_mark)
        }
    }

    ///
    fun setIconsWhenSwiping(prePos: Int, nextPos: Int) {
        notifyItemChanged(prePos)
        notifyItemChanged(nextPos)
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
                holder.questionImg.setImageResource(R.drawable.orange_question_mark)
                wordList[position].wordState = false
                reviewWords.changeNumOfMistakes(true)
                reviewWords.changeNumOfRemaining(false)
            } else if (wordList[position].wordState == true) {
                wordList[position].wordState = !wordList[position].wordState!!
                holder.questionImg.setImageResource(R.drawable.orange_question_mark)
                holder.checkImg.setImageResource(R.drawable.check_icon)
                reviewWords.changeNumOfMistakes(true)
                reviewWords.changeNumOfStudied(false)
            } else if (wordList[position].wordState != true) {
                wordList[position].wordState = null
                holder.questionImg.setImageResource(R.drawable.question_icon)
                reviewWords.changeNumOfMistakes(false)
                reviewWords.changeNumOfRemaining(true)
            }
        }
    }

    private fun checkCardClick(
        holder: WordVH,
        position: Int
    ) {
        holder.checkCard.setOnClickListener {
            if (wordList[position].wordState == null) {
                holder.checkImg.setImageResource(R.drawable.green_check_logo)
                wordList[position].wordState = true
                reviewWords.changeNumOfStudied(true)
                reviewWords.changeNumOfRemaining(false)
            } else if (wordList[position].wordState == true) {
                wordList[position].wordState = null
                holder.checkImg.setImageResource(R.drawable.check_icon)
                reviewWords.changeNumOfStudied(false)
                reviewWords.changeNumOfRemaining(true)
            } else if (wordList[position].wordState != true) {
                wordList[position].wordState = !wordList[position].wordState!!
                holder.checkImg.setImageResource(R.drawable.green_check_logo)
                holder.questionImg.setImageResource(R.drawable.question_icon)
                reviewWords.changeNumOfMistakes(false)
                reviewWords.changeNumOfStudied(true)
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
                val size = holder.meaning.measuredHeight
                resizeWordCard(holder, size + 110)
                changeEyeIcon(true, holder)
            } else {
                resizeWordCard(holder, 80)
                holder.showAnswerImg.setImageResource(R.drawable.close_eye_icon)
                holder.showAnswerImg.setBackgroundResource(R.color.white)
                changeEyeIcon(false, holder)

            }
        }
    }

    private fun init(
        holder: WordVH,
        position: Int
    ) {
        holder.word.text = wordList[position].word
        holder.meaning.text = wordList[position].meaning
        holder.meaning.bringToFront()
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

    fun resizeWordCard(holder: WordVH, size: Int) {
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
        a.duration = 190
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
        val questionImg : AppCompatImageView = itemView.findViewById(R.id.question_img)
        val checkCard : CardView = itemView.findViewById(R.id.check_card)
        val questionCard : CardView = itemView.findViewById(R.id.question_card)
    }
}