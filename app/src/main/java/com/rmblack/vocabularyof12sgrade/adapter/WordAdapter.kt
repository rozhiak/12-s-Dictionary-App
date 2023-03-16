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
import com.rmblack.vocabularyof12sgrade.models.Word

class WordAdapter(private val wordList : ArrayList<Word>)
    : RecyclerView.Adapter<WordAdapter.WordVH>() {

    private lateinit var holder: WordVH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordVH {
        val view = LayoutInflater.from(parent.context).inflate(com.rmblack.vocabularyof12sgrade.R.layout.word_row, parent, false)
        return WordVH(view)
    }

    override fun onBindViewHolder(holder: WordVH, position: Int) {
        this.holder = holder
        Init(holder, position)

        val word : Word = wordList[position]

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

    private fun Init(
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
        val params = holder.wordCard.layoutParams as ViewGroup.MarginLayoutParams
        val bottomMarginStart = params.bottomMargin // your start value
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