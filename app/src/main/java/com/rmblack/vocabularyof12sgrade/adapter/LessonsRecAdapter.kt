package com.rmblack.vocabularyof12sgrade.adapter

import android.graphics.Color
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.rmblack.vocabularyof12sgrade.R
import com.rmblack.vocabularyof12sgrade.logic.Lesson
import com.rmblack.vocabularyof12sgrade.server.RetrofitHelper
import com.rmblack.vocabularyof12sgrade.server.WordsApi
import com.rmblack.vocabularyof12sgrade.server.models.URL
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Callback
import retrofit2.Response

class LessonsRecAdapter(private val lessons: List<Lesson>) : RecyclerView.Adapter<LessonsRecAdapter.ViewHolder>() {

    lateinit var recyclerView : RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    inner class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val lessonNumberTV : AppCompatTextView = itemView.findViewById(R.id.lesson_number_tv)
        val lessonTitleTV : AppCompatTextView = itemView.findViewById(R.id.lesson_title_tv)
        val lessonImage : AppCompatImageView = itemView.findViewById(R.id.lesson_img)
        val lessonConstraint : ConstraintLayout = itemView.findViewById(R.id.lesson_constraint)
        val expandedLayout : ConstraintLayout = itemView.findViewById(R.id.expanded_layout)
        val lessonCard : CardView = itemView.findViewById(R.id.lesson_card)
        val oneMistakeSwitch : SwitchCompat = itemView.findViewById(R.id.one_mistake_switch)
        val twoMistakeSwitch : SwitchCompat = itemView.findViewById(R.id.two_mistakes_switch)
        val threeMistakeSwitch : SwitchCompat = itemView.findViewById(R.id.three_mistakes_switch)
        val moreThanThreeMistakesSwitch : SwitchCompat = itemView.findViewById(R.id.more_than_three_mistakes_switch)
        val startReviewCard : CardView = itemView.findViewById(R.id.startReviewCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val lessonView = inflater.inflate(R.layout.item_lesson_row, parent, false)
        return ViewHolder(lessonView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.oneMistakeSwitch.isChecked = false
        holder.twoMistakeSwitch.isChecked = false
        holder.threeMistakeSwitch.isChecked = false
        holder.moreThanThreeMistakesSwitch.isChecked = false

        val lesson : Lesson = lessons[position]
        holder.lessonConstraint.setOnClickListener {
            if (!lesson.visibility) {
                for (i in lessons.indices) {
                    if (i != position) {
                        if (lessons[i].visibility) {
                            lessons[i].visibility = !lessons[i].visibility
                            notifyItemChanged(i)
                        }
                    }
                }
            }
            lesson.visibility = !lesson.visibility

            notifyItemChanged(position)
            recyclerView.post {
                recyclerView.smoothScrollToPosition(position)
            }
        }

        if (lesson.visibility) {
            holder.expandedLayout.visibility = View.VISIBLE
            holder.lessonCard.setCardBackgroundColor(Color.parseColor("#ECEFF7"))
        } else {
            holder.expandedLayout.visibility = View.GONE
            holder.lessonCard.setCardBackgroundColor(Color.parseColor("#F8F9F9"))
        }

        holder.lessonImage.setBackgroundResource(lesson.lessonImage)
        holder.lessonNumberTV.text = lesson.number
        holder.lessonTitleTV.text = lesson.title




        holder.startReviewCard.setOnClickListener {
            val urlsApi = RetrofitHelper.getInstance().create(WordsApi::class.java)
            val call: retrofit2.Call<ArrayList<URL>> = urlsApi.getURLs()
            call.enqueue(object : Callback<ArrayList<URL>> {
                override fun onResponse(
                    call: retrofit2.Call<ArrayList<URL>>,
                    response: Response<ArrayList<URL>>
                ) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            Log.e("urls", response.body()!![position].toString())
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<ArrayList<URL>>, t: Throwable) {

                }
            })
        }





    }


    override fun getItemCount(): Int {
        return lessons.size
    }

}