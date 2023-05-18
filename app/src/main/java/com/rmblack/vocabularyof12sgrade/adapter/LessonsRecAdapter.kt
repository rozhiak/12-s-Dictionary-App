package com.rmblack.vocabularyof12sgrade.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.rmblack.vocabularyof12sgrade.R
import com.rmblack.vocabularyof12sgrade.activities.ReviewWords
import com.rmblack.vocabularyof12sgrade.databinding.ActivityMainBinding
import com.rmblack.vocabularyof12sgrade.models.Lesson
import com.rmblack.vocabularyof12sgrade.models.URL
import com.rmblack.vocabularyof12sgrade.models.Word
import com.rmblack.vocabularyof12sgrade.server.IService
import com.rmblack.vocabularyof12sgrade.server.RetrofitHelper
import com.rmblack.vocabularyof12sgrade.utils.DataBaseInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LessonsRecAdapter(private val lessons: List<Lesson>, private val context: Context, private val binding: ActivityMainBinding) : RecyclerView.Adapter<LessonsRecAdapter.ViewHolder>() {

    private lateinit var recyclerView : RecyclerView
    private val sp = context.getSharedPreferences(DataBaseInfo.SP_NAME, Context.MODE_PRIVATE)

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
        val firstLoadingStartBtn: CircularProgressButton = itemView.findViewById(R.id.first_loading_start_btn)
        val secondLoadingStartBtn: CircularProgressButton = itemView.findViewById(R.id.second_loading_start_btn)
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
        holder.firstLoadingStartBtn.setOnClickListener {
            holder.firstLoadingStartBtn.startAnimation()
            reviewWords(holder)
        }
        holder.secondLoadingStartBtn.setOnClickListener {
            reviewRepeatedMistakeWords(holder)
        }
    }

    private fun reviewRepeatedMistakeWords(holder: ViewHolder) {
        val oneMis = holder.oneMistakeSwitch.isChecked
        val twoMis = holder.twoMistakeSwitch.isChecked
        val threeMis = holder.threeMistakeSwitch.isChecked
        val threeAndMoreMis = holder.moreThanThreeMistakesSwitch.isChecked

        if (!oneMis && !twoMis && !threeMis && !threeAndMoreMis) {
            //Say to user that he/she should select number of mistakes.
            makeSnack("حداقل باید یکی از گزینه های یک اشتباه ، دو اشتباه و... رو انتخاب کنید.")
        } else {
            holder.secondLoadingStartBtn.startAnimation()
            val tarPos = holder.bindingAdapterPosition
            if (sp.contains(lessons[tarPos].title)) {
                val words = getWordsFromDB(tarPos)
                val wordsToReview : ArrayList<Word> = ArrayList()
                lessons[tarPos].words = words
                collectWords(words, oneMis, wordsToReview, twoMis, threeMis, threeAndMoreMis)
                if (wordsToReview.size == 0) {
                    holder.secondLoadingStartBtn.revertAnimation()
                    //Say to user the there is no word with repeated mistakes
                    makeSnack("کلمه ای مطابق با تعداد اشتباهات یافت نشد.")
                } else {
                    lessons[tarPos].wordsToReview = wordsToReview
                    holder.secondLoadingStartBtn.revertAnimation()
                    startReviewActivity(holder)
                }
            } else {
                holder.secondLoadingStartBtn.revertAnimation()
                //Say to user that he/she should review all words at least 1 time then review repeated mistake words.
                makeSnack("برای مرور کلمات پر اشتباه ، حداقل یکبار باید مرورکلی درس را انجام داده باشید.")
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun makeSnack(text: String) {
        val snackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT)
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customSnackView: View = layoutInflater.inflate(R.layout.snackbar_layout, null)
        val textView = customSnackView.findViewById<AppCompatTextView>(R.id.description)
        textView.text = text
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        snackBarLayout.setPadding(5, 0, 5, 0)
        snackBarLayout.addView(customSnackView, 0)
        snackBar.show()
    }

    private fun collectWords(
        words: java.util.ArrayList<Word>,
        oneMis: Boolean,
        wordsToReview: ArrayList<Word>,
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

    private fun setEachLesson(
        position: Int,
        holder: ViewHolder
    ) {
        val lesson: Lesson = lessonClick(position, holder)
        lessonVisibility(lesson, holder)
        lessonInfo(holder, lesson)
        saveSwitchesState(holder)
    }

    private fun saveSwitchesState(
        holder: ViewHolder
    ) {
        val lesson = lessons[holder.absoluteAdapterPosition]
        holder.oneMistakeSwitch.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isPressed) {
                lesson.firstSwitch = holder.oneMistakeSwitch.isChecked
            }
        }
        holder.twoMistakeSwitch.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isPressed) {
                lesson.secondSwitch = holder.twoMistakeSwitch.isChecked
            }
        }
        holder.threeMistakeSwitch.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isPressed) {
                lesson.thirdSwitch = holder.threeMistakeSwitch.isChecked
            }
        }
        holder.moreThanThreeMistakesSwitch.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isPressed) {
                lesson.forthSwitch = holder.moreThanThreeMistakesSwitch.isChecked
            }
        }
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
            holder.firstLoadingStartBtn.visibility = View.VISIBLE
            holder.secondLoadingStartBtn.visibility = View.VISIBLE
            holder.lessonCard.setCardBackgroundColor(Color.parseColor("#ECEFF7"))
        } else {
            holder.expandedLayout.visibility = View.GONE
            holder.firstLoadingStartBtn.visibility = View.GONE
            holder.secondLoadingStartBtn.visibility = View.GONE
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
            setStartBtnsToDefault(holder)
            notifyItemChanged(position)
            recyclerView.post {
                recyclerView.smoothScrollToPosition(position)
            }
        }
        return lesson
    }

    private fun setStartBtnsToDefault(holder: ViewHolder) {
        holder.firstLoadingStartBtn.revertAnimation()
        holder.secondLoadingStartBtn.revertAnimation()
    }

    private fun setSwitchesToDefault(holder: ViewHolder) {
        holder.oneMistakeSwitch.isChecked = lessons[holder.bindingAdapterPosition].firstSwitch
        holder.twoMistakeSwitch.isChecked = lessons[holder.bindingAdapterPosition].secondSwitch
        holder.threeMistakeSwitch.isChecked = lessons[holder.bindingAdapterPosition].thirdSwitch
        holder.moreThanThreeMistakesSwitch.isChecked = lessons[holder.bindingAdapterPosition].forthSwitch
    }

    private fun reviewWords(holder: ViewHolder) {
        if (sp.contains(lessons[holder.bindingAdapterPosition].title)) {
            getAndFetchDataToLesson(holder)
        } else {
            getDestinations(holder)
        }
    }

    private fun getAndFetchDataToLesson(holder: ViewHolder) {
        val wordList: java.util.ArrayList<Word> =
            getWordsFromDB(holder.bindingAdapterPosition)
        if (lessons[holder.bindingAdapterPosition].words == null) {
            lessons[holder.bindingAdapterPosition].words = wordList
        }
        lessons[holder.bindingAdapterPosition].wordsToReview = wordList
        holder.firstLoadingStartBtn.revertAnimation()
        startReviewActivity(holder)
    }

    private fun getWordsFromDB(tarPos: Int): java.util.ArrayList<Word> {
        return Json.decodeFromString(
            sp.getString(lessons[tarPos].title, "").toString()
        )
    }

    private fun getDestinations(holder: ViewHolder) {
        val urlsApi = RetrofitHelper.getInstance().create(IService::class.java)
        val call: Call<ArrayList<URL>> = urlsApi.getURLs()
        call.enqueue(object : Callback<ArrayList<URL>> {
            override fun onResponse(
                call: Call<ArrayList<URL>>,
                response: Response<ArrayList<URL>>
            ) {
                if (response.code() == 200 && response.isSuccessful && response.body() != null) {
                    getWords(response, holder)
                } else {
                    //server error
                    holder.firstLoadingStartBtn.revertAnimation()
                    makeSnack("مشکلی در ارتبات با اینترنت پیش آمده ، از اتصال اینترنت خود مطمئن شوید.")
                }
            }
            override fun onFailure(call: Call<ArrayList<URL>>, t: Throwable) {
                holder.firstLoadingStartBtn.revertAnimation()
                makeSnack("مشکلی در ارتبات با اینترنت پیش آمده ، از اتصال اینترنت خود مطمئن شوید.")

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
        if (holder.bindingAdapterPosition < response.body()!!.size) {
            val wordCall: Call<ArrayList<Word>> = retrofit.getWords(response.body()!![holder.bindingAdapterPosition].wordsURL)
            wordCall.enqueue(object : Callback<ArrayList<Word>> {
                override fun onResponse(
                    call: Call<ArrayList<Word>>,
                    response: Response<ArrayList<Word>>
                ) {
                    if (response.code() == 200 && response.isSuccessful && response.body() != null) {
                        saveWordsToDB(response, holder.bindingAdapterPosition)
                        saveWordsToLesson(holder.bindingAdapterPosition, response)
                        holder.firstLoadingStartBtn.revertAnimation()
                        startReviewActivity(holder)
                    } else {
                        //server error
                        holder.firstLoadingStartBtn.revertAnimation()
                        makeSnack("مشکلی در ارتبات با اینترنت پیش آمده ، از اتصال اینترنت خود مطمئن شوید.")
                    }
                }
                override fun onFailure(call: Call<ArrayList<Word>>, t: Throwable) {
                    holder.firstLoadingStartBtn.revertAnimation()
                    makeSnack("مشکلی در ارتبات با اینترنت پیش آمده ، از اتصال اینترنت خود مطمئن شوید.")
                }
            })
        } else {
            //A problem in server is occurred
            holder.firstLoadingStartBtn.revertAnimation()
            makeSnack("مشکلی در سرور پیش آمده در حال رفع آن هستیم.")
        }
    }

    private fun saveWordsToLesson(
        tarPos: Int,
        response: Response<ArrayList<Word>>
    ) {
        lessons[tarPos].words = response.body()
        lessons[tarPos].wordsToReview = response.body()
    }

    private fun startReviewActivity(holder: ViewHolder) {
        val serializesLesson = Json.encodeToString(lessons[holder.bindingAdapterPosition])
        val intent = Intent(context, ReviewWords::class.java)
        intent.putExtra(DataBaseInfo.BUNDLE_LESSON, serializesLesson)
        context.startActivity(intent)
    }

    private fun saveWordsToDB(
        response: Response<ArrayList<Word>>,
        tarPos: Int
    ) {
        val editor = sp.edit()
        val serializesArray = Json.encodeToString(response.body())
        editor.putString(lessons[tarPos].title, serializesArray)
        editor.apply()
    }

    override fun getItemCount(): Int {
        return lessons.size
    }
}