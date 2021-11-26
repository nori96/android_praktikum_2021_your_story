package com.example.yourstory.diary

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.github.mikephil.charting.charts.PieChart

class DiaryAdapter() : RecyclerView.Adapter<DiaryAdapter.ViewHolder>(){

    var dataSet: ArrayList<DiaryModel> = arrayListOf()

    class ViewHolder(diaryView: View) : RecyclerView.ViewHolder(diaryView){
        val pieChart: PieChart
        val textViewDate: TextView
        val textViewEntries: TextView

        init {
            textViewDate = diaryView.findViewById(R.id.diary_item_date)
            pieChart = diaryView.findViewById(R.id.diary_pieChart)
            textViewEntries = diaryView.findViewById(R.id.diary_item_entries)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        val diaryView = inflater.inflate(R.layout.diary_row_item,viewGroup,false)
        return ViewHolder(diaryView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewDate.text = dataSet[position].date
        viewHolder.textViewEntries.text = "Entries:" + dataSet[position].entries.toString()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


}