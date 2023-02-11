package com.rmblack.vocabularyof12sgrade.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rmblack.vocabularyof12sgrade.logic.Word


class WordAdapter(private val wordList : ArrayList<Word>)
    : RecyclerView.Adapter<WordAdapter.WordVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordVH {
        val view = LayoutInflater.from(parent.context).inflate(com.rmblack.vocabularyof12sgrade.R.layout.word_row, parent, false)
        return WordVH(view)
    }

    override fun onBindViewHolder(holder: WordVH, position: Int) {
        holder.word.text = wordList[position].word
        holder.meaning.text = wordList[position].meaning
        holder.meaning.bringToFront()

        val word : Word = wordList[position]

        holder.showAnswerCard.setOnClickListener {
            word.answerVisibility = !word.answerVisibility
            if (word.answerVisibility) {
                val size = holder.meaning.measuredHeight
                resizeWordCard(holder, size + 110)
            } else {
                resizeWordCard(holder, 80)
            }
        }
    }

    private fun resizeWordCard(holder: WordVH, size: Int) {
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
        val word : AppCompatTextView = itemView.findViewById(com.rmblack.vocabularyof12sgrade.R.id.word_tv)
        val wordCard : CardView = itemView.findViewById(com.rmblack.vocabularyof12sgrade.R.id.word_card)
        val meaning : AppCompatTextView = itemView.findViewById(com.rmblack.vocabularyof12sgrade.R.id.meaning_tv)
        val showAnswerCard : CardView = itemView.findViewById(com.rmblack.vocabularyof12sgrade.R.id.show_answer_card)
    }
}