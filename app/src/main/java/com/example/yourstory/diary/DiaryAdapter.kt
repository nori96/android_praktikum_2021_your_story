package com.example.yourstory.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.github.mikephil.charting.charts.PieChart

class DiaryAdapter(var onDiaryClickListener: OnDiaryClickListener) : RecyclerView.Adapter<DiaryAdapter.ViewHolder>(){

    var dataSet: ArrayList<DiaryListModel> = arrayListOf()


    class ViewHolder(diaryView: View, onDiaryClickListener: OnDiaryClickListener) : RecyclerView.ViewHolder(diaryView), View.OnClickListener{
        val pieChart: PieChart
        val textViewDate: TextView
        val textViewEntries: TextView
        var onDiaryClickListener: OnDiaryClickListener

        init {
            diaryView.setOnClickListener(this)
            this.onDiaryClickListener = onDiaryClickListener
            textViewDate = diaryView.findViewById(R.id.diary_item_date)
            pieChart = diaryView.findViewById(R.id.diary_pieChart)
            textViewEntries = diaryView.findViewById(R.id.diary_item_entries)
        }

        override fun onClick(v: View?) {
            onDiaryClickListener.onNoteClick(bindingAdapterPosition)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        val diaryView = inflater.inflate(R.layout.diary_row_item,viewGroup,false)
        return ViewHolder(diaryView, onDiaryClickListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewDate.text = dataSet[position].date.toString()
        viewHolder.textViewEntries.text = "Entries:" + dataSet[position].entries.toString()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    interface OnDiaryClickListener{
        fun onNoteClick(position: Int)
    }

}