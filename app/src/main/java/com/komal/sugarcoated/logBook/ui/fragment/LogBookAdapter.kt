package com.komal.sugarcoated.logBook.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.komal.sugarcoated.R
import com.komal.sugarcoated.addNewRecord.model.RecordDataModel
import com.komal.sugarcoated.utils.getTimeFromDate

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
    holder.time.text = getTimeFromDate(record.dateAndTime.trimIndent())

    val categoryIcon:Int =

      when(record.category){

      "Breakfast" -> {
        R.drawable.icon_breakfast
      }
      "Lunch"     -> {
        R.drawable.icon_lunch
      }
      "Snack"     -> {
        R.drawable.icon_snack
      }
      "Dinner"    -> {
        R.drawable.icon_dinner
      }
      "Fasting"   -> {
        R.drawable.icon_sun
      }
      "Random"    -> {
        R.drawable.icon_random
      }
      "Bed Time"  -> {
        R.drawable.icon_before_bed
      }
      else -> {
        R.drawable.icon_happy
      }

    }

    holder.categoryIcon.setImageResource(categoryIcon)
    if(record.note == ""){
      holder.note.visibility = GONE
    }else{
      holder.note.visibility = VISIBLE
      holder.note.text = record.note
    }

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