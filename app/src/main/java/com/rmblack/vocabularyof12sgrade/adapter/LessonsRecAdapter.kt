package com.rmblack.vocabularyof12sgrade.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.rmblack.vocabularyof12sgrade.Utilities.DataBaseInfo
import com.rmblack.vocabularyof12sgrade.activities.ReviewWords
import com.rmblack.vocabularyof12sgrade.models.Lesson
import com.rmblack.vocabularyof12sgrade.models.URL
import com.rmblack.vocabularyof12sgrade.models.Word
import com.rmblack.vocabularyof12sgrade.server.IService
import com.rmblack.vocabularyof12sgrade.server.RetrofitHelper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LessonsRecAdapter(private val lessons: List<Lesson>, private val context: Context) : RecyclerView.Adapter<LessonsRecAdapter.ViewHolder>() {

    private lateinit var recyclerView : RecyclerView

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
        setSwitchesToDefault(holder)
        setEachLesson(position, holder)
        holder.startReviewCard.setOnClickListener {
            reviewWords(holder)
        }
    }

    private fun setEachLesson(
        position: Int,
        holder: ViewHolder
    ) {
        val lesson: Lesson = lessonClick(position, holder)
        lessonVisibility(lesson, holder)
        lessonInfo(holder, lesson)
    }

    private fun lessonInfo(
        holder: ViewHolder,
        lesson: Lesson
    ) {
        holder.lessonImage.setBackgroundResource(lesson.lessonImage)
        holder.lessonNumberTV.text = lesson.number
        holder.lessonTitleTV.text = lesson.title
    }

    private fun lessonVisibility(
        lesson: Lesson,
        holder: ViewHolder
    ) {
        if (lesson.visibility) {
            holder.expandedLayout.visibility = View.VISIBLE
            holder.lessonCard.setCardBackgroundColor(Color.parseColor("#ECEFF7"))
        } else {
            holder.expandedLayout.visibility = View.GONE
            holder.lessonCard.setCardBackgroundColor(Color.parseColor("#F8F9F9"))
        }
    }

    private fun lessonClick(
        position: Int,
        holder: ViewHolder
    ): Lesson {
        val lesson: Lesson = lessons[position]
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
        return lesson
    }

    private fun setSwitchesToDefault(holder: ViewHolder) {
        holder.oneMistakeSwitch.isChecked = false
        holder.twoMistakeSwitch.isChecked = false
        holder.threeMistakeSwitch.isChecked = false
        holder.moreThanThreeMistakesSwitch.isChecked = false
    }

    private fun reviewWords(holder: ViewHolder) {
        getDestinations(holder)
    }

    private fun getDestinations(holder: ViewHolder) {
        val urlsApi = RetrofitHelper.getInstance().create(IService::class.java)
        val call: Call<ArrayList<URL>> = urlsApi.getURLs()
        call.enqueue(object : Callback<ArrayList<URL>> {
            override fun onResponse(
                call: Call<ArrayList<URL>>,
                response: Response<ArrayList<URL>>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null && response.isSuccessful) {
                        Log.e("urls", response.body()!![holder.bindingAdapterPosition].toString())
                        getWords(response, holder)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<URL>>, t: Throwable) {

            }
        })
    }

    private fun getWords(
        response: Response<ArrayList<URL>>,
        holder: ViewHolder
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-generator.retool.com/")
            .addConverterFactory(GsonConverterFactory.create()).build().create(IService::class.java)
        val wordCall: Call<ArrayList<Word>> = retrofit.getWords(response.body()!![holder.bindingAdapterPosition].wordsURL)
        wordCall.enqueue(object : Callback<ArrayList<Word>> {
            override fun onResponse(
                call: Call<ArrayList<Word>>,
                response: Response<ArrayList<Word>>
            ) {
                saveWordsToDB(response, holder)
                startReviewActivity(holder)
            }

            override fun onFailure(call: Call<ArrayList<Word>>, t: Throwable) {
                Log.e("urls", t.toString())
            }
        })
    }

    private fun startReviewActivity(holder: ViewHolder) {
        val serializesLesson = Json.encodeToString(lessons[holder.bindingAdapterPosition])
        val intent = Intent(context, ReviewWords::class.java)
        intent.putExtra(DataBaseInfo.BUNDLE_LESSON, serializesLesson)
        context.startActivity(intent)
    }

    private fun saveWordsToDB(
        response: Response<ArrayList<Word>>,
        holder: ViewHolder
    ) {
        val sp = context.getSharedPreferences(DataBaseInfo.SP_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        val serializesArray = Json.encodeToString(response.body())
        editor.putString(lessons[holder.bindingAdapterPosition].title, serializesArray)
        editor.apply()


    }

    override fun getItemCount(): Int {
        return lessons.size
    }

}