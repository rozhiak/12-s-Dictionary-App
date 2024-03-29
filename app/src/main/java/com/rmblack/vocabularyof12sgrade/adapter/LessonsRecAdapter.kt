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
import com.rmblack.vocabularyof12sgrade.models.*
import com.rmblack.vocabularyof12sgrade.server.IService
import com.rmblack.vocabularyof12sgrade.server.RetrofitHelper
import com.rmblack.vocabularyof12sgrade.utils.DataBaseInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LessonsRecAdapter(private val lessons: List<Lesson>,
                        private val context: Context,
                        private val binding: ActivityMainBinding) : RecyclerView.Adapter<LessonsRecAdapter.ViewHolder>() {

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
        val oneMisTv: AppCompatTextView = itemView.findViewById(R.id.one_mis_tv)
        val twoMisTv: AppCompatTextView = itemView.findViewById(R.id.two_mis_tv)
        val threeMisTv: AppCompatTextView = itemView.findViewById(R.id.three_mis_tv)
        val moreThreeMisTv: AppCompatTextView = itemView.findViewById(R.id.more_three_mis_tv)
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
            reviewAllWords(holder)
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
                val anyMistake : Boolean = anyMistake(words, oneMis, twoMis, threeMis, threeAndMoreMis)
                if (anyMistake) {
                    val intentDataPack = ReviewIntentDataPack(lessons[tarPos].title,
                        ReviewType.REVIEW_MISTAKES,
                        oneMis, twoMis, threeMis, threeAndMoreMis)
                    val serializedDataPack = Json.encodeToString(intentDataPack)
                    val intent = Intent(context, ReviewWords::class.java)
                    intent.putExtra(DataBaseInfo.BUNDLE_REVIEW_DATA_PACK, serializedDataPack)
                    holder.secondLoadingStartBtn.revertAnimation()
                    context.startActivity(intent)
                } else {
                    holder.secondLoadingStartBtn.revertAnimation()
                    //Say to user the there is no word with repeated mistakes
                    makeSnack("کلمه ای مطابق با تعداد اشتباهات مشخص شده یافت نشد.")
                }
            } else {
                holder.secondLoadingStartBtn.revertAnimation()
                //Say to user that he/she should review all words at least 1 time then review repeated mistake words.
                makeSnack("برای مرور کلمات پر اشتباه ، حداقل یکبار باید مرورکلی درس را انجام داده باشید.")
            }
        }
    }

    private fun anyMistake(
        words: java.util.ArrayList<Word>,
        oneMis: Boolean,
        twoMis: Boolean,
        threeMis: Boolean,
        threeAndMoreMis: Boolean
    ) : Boolean {
        for (w in words) {
            if (oneMis && w.wrongNum == 1) {
                return true
            }
            if (twoMis && w.wrongNum == 2) {
                return true
            }
            if (threeMis && w.wrongNum == 3) {
                return true
            }
            if (threeAndMoreMis && w.wrongNum > 3) {
                return true
            }
        }
        return false
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

    private fun setEachLesson(
        position: Int,
        holder: ViewHolder
    ) {
        val lesson: Lesson = lessonClick(position, holder)
        lessonVisibility(lesson, holder)
        lessonInfo(holder, lesson)
        setSwitchesPressEvent(holder)
    }

    private fun setSwitchesPressEvent(
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
        holder.oneMisTv.setOnClickListener {
            holder.oneMistakeSwitch.isChecked = !holder.oneMistakeSwitch.isChecked
        }
        holder.twoMisTv.setOnClickListener {
            holder.twoMistakeSwitch.isChecked = !holder.twoMistakeSwitch.isChecked
        }
        holder.threeMisTv.setOnClickListener {
            holder.threeMistakeSwitch.isChecked = !holder.threeMistakeSwitch.isChecked
        }
        holder.moreThreeMisTv.setOnClickListener {
            holder.moreThanThreeMistakesSwitch.isChecked = !holder.moreThanThreeMistakesSwitch.isChecked
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

    private fun reviewAllWords(holder: ViewHolder) {
        if (sp.contains(lessons[holder.bindingAdapterPosition].title)) {
            startReview(holder.bindingAdapterPosition, holder)
        } else {
            val position = holder.bindingAdapterPosition
            getWords(holder, position)
        }
    }

    private fun getWordsFromDB(tarPos: Int): java.util.ArrayList<Word> {
        return Json.decodeFromString(
            sp.getString(lessons[tarPos].title, "").toString()
        )
    }

    private fun getWords(
        holder: ViewHolder,
        position: Int
    ) {
        val retrofit = RetrofitHelper.getInstance().create(IService::class.java)
        val wordCall: Call<ArrayList<Word>> = retrofit.getWords(lessons[position].relativeWordsURL)
        wordCall.enqueue(object : Callback<ArrayList<Word>> {
            override fun onResponse(
                call: Call<ArrayList<Word>>,
                response: Response<ArrayList<Word>>
            ) {
                if (response.code() == 200 && response.isSuccessful && response.body() != null) {
                    saveWordsToDB(response, position)
                    startReview(position, holder)
                } else {
                    //server error
                    holder.firstLoadingStartBtn.revertAnimation()
                    makeSnack("مشکلی در سرور پیش آمده ، در حال رفع آن هستیم.")
                }
            }
            override fun onFailure(call: Call<ArrayList<Word>>, t: Throwable) {
                holder.firstLoadingStartBtn.revertAnimation()
                if (t.message == "Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 1 column 2 path $") {
                    makeSnack("مشکلی در سرور پیش آمده ، در حال رفع آن هستیم.")
                } else {
                    makeSnack("از اتصال اینترنت خود مطمئن شوید ، سپس مجددا تلاش کنید.")
                }
            }
        })
    }

    private fun startReview(
        position: Int,
        holder: ViewHolder
    ) {
        val intentDataPack =
            ReviewIntentDataPack(
                lessons[position].title,
                ReviewType.REVIEW_ALL,
                null, null, null, null
            )
        val serializedDataPack = Json.encodeToString(intentDataPack)
        val intent = Intent(context, ReviewWords::class.java)
        intent.putExtra(DataBaseInfo.BUNDLE_REVIEW_DATA_PACK, serializedDataPack)
        holder.firstLoadingStartBtn.revertAnimation()
        context.startActivity(intent)
    }

    private fun saveWordsToDB(
        response: Response<ArrayList<Word>>,
        tarPos: Int
    ) {
        val editor = sp.edit()
        val serializedArray = Json.encodeToString(response.body())
        editor.putString(lessons[tarPos].title, serializedArray)
        editor.apply()
    }

    override fun getItemCount(): Int {
        return lessons.size
    }
}