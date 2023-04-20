package com.komal.sugarcoated.logBook.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.komal.sugarcoated.R
import com.komal.sugarcoated.addNewRecord.model.RecordDataModel
import com.komal.sugarcoated.utils.Constants.BED_TIME
import com.komal.sugarcoated.utils.Constants.BREAKFAST
import com.komal.sugarcoated.utils.Constants.DINNER
import com.komal.sugarcoated.utils.Constants.FASTING
import com.komal.sugarcoated.utils.Constants.LUNCH
import com.komal.sugarcoated.utils.Constants.RANDOM
import com.komal.sugarcoated.utils.Constants.SNACK
import com.komal.sugarcoated.utils.Constants.TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LogBookAdapter(private val recordList: ArrayList<RecordDataModel>)
      : RecyclerView.Adapter<LogBookAdapter.LogBookViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogBookViewHolder {

    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.records_list_item,
                                                                parent,
                                                                false)

    return LogBookViewHolder(itemView)

  }

  override fun onBindViewHolder(holder: LogBookViewHolder, position: Int) {

    val record = recordList[holder.adapterPosition]
    holder.bloodSugarValue.text = record.bloodSugarValue
    holder.time.text = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(record.dateAndTime)

    val categoryIcon:Int =

      when(record.category){

      BREAKFAST -> {
        R.drawable.icon_breakfast
      }
      LUNCH     -> {
        R.drawable.icon_lunch
      }
      SNACK     -> {
        R.drawable.icon_snack
      }
      DINNER    -> {
        R.drawable.icon_dinner
      }
      FASTING   -> {
        R.drawable.icon_sun
      }
      RANDOM    -> {
        R.drawable.icon_random
      }
      BED_TIME  -> {
        R.drawable.icon_before_bed
      }
      else -> {
        R.drawable.icon_happy
      }

    }

    holder.categoryIcon.setImageResource(categoryIcon)
    holder.note.text = record.note

  }

  override fun getItemCount(): Int {

    return recordList.size

  }

  class LogBookViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val bloodSugarValue: TextView = itemView.findViewById(R.id.tv_blood_glucose_value)
    val time: TextView            = itemView.findViewById(R.id.tv_time)
    val categoryIcon: ImageView   = itemView.findViewById(R.id.iv_category_icon)
    val note: TextView            = itemView.findViewById(R.id.tv_note)

  }

}