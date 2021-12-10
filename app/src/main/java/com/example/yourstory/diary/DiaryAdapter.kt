package com.example.yourstory.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

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
        var diaryListModelItem = dataSet[position]

        //No Likert-Rating was Done
        if(dataSet[position].angerAverage == 0F && dataSet[position].disgustAverage == 0F && dataSet[position].fearAverage == 0F && dataSet[position].joyAverage == 0F && dataSet[position].sadnessAverage == 0F && dataSet[position].supriseAverage == 0F){
            var pieEntries = arrayListOf<PieEntry>()
            var typeAmountMap = HashMap<String,Float>()
            typeAmountMap.put("Empty",1F)
            var colors = arrayListOf<Int>(ColorTemplate.rgb("#D3D3D3"))
            for((name,average) in typeAmountMap){
                pieEntries.add(PieEntry(average,name))
            }
            var pieDataSet = PieDataSet(pieEntries,"Empty")
            pieDataSet.colors = colors
            pieDataSet.setDrawValues(false)

            viewHolder.pieChart.data = PieData(pieDataSet)
            viewHolder.pieChart.setDrawEntryLabels(false)
            viewHolder.pieChart.legend.isEnabled = false
            viewHolder.pieChart.description.isEnabled = false
            viewHolder.pieChart.isDrawHoleEnabled = false
            viewHolder.pieChart.invalidate()


        }else {
            var diaryListModelItem = dataSet[position]
            ////---Creating the PieChart---////
            var pieEntries = arrayListOf<PieEntry>()

            //initialize data
            var typeAmountMap = HashMap<String, Float>()
            typeAmountMap.put("Joy", diaryListModelItem.joyAverage)
            typeAmountMap.put("Anger", diaryListModelItem.angerAverage)
            typeAmountMap.put("Surprise", diaryListModelItem.supriseAverage)
            typeAmountMap.put("Sadness", diaryListModelItem.sadnessAverage)
            typeAmountMap.put("Disgust", diaryListModelItem.disgustAverage)
            typeAmountMap.put("Fear", diaryListModelItem.fearAverage)

            //initializing colors for the entries
            var colors = arrayListOf<Int>()
            colors.add(ColorTemplate.rgb("#4BF430"))
            colors.add(ColorTemplate.rgb("#FF8181"))
            colors.add(ColorTemplate.rgb("#FEF63B"))
            colors.add(ColorTemplate.rgb("#7BB8FF"))
            colors.add(ColorTemplate.rgb("#869200"))
            colors.add(ColorTemplate.rgb("#B960FF"))

            //input data and fit data into pie chart entry
            for ((name, average) in typeAmountMap) {
                pieEntries.add(PieEntry(average, name))
            }

            //collecting the entries with label name
            var pieDataSet = PieDataSet(pieEntries, "Emotions")

            //setting text size of the value
            pieDataSet.valueTextSize = 12f;

            //providing color list for coloring different entries
            pieDataSet.colors = colors

            //grouping the data set from entry to chart
            var pieData = PieData(pieDataSet)

            //showing the value of the entries, default true if not set
            pieData.setDrawValues(false)


            viewHolder.pieChart.data = pieData
            viewHolder.pieChart.setDrawEntryLabels(false)
            viewHolder.pieChart.legend.isEnabled = false
            viewHolder.pieChart.description.isEnabled = false

            viewHolder.pieChart.invalidate()
        }

        viewHolder.textViewDate.text = diaryListModelItem.date.toString()
        viewHolder.textViewEntries.text = "Entries: ${diaryListModelItem.entries}"
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    interface OnDiaryClickListener{
        fun onNoteClick(position: Int)
    }

    public fun setData(data: ArrayList<DiaryListModel>){
        this.dataSet = data
        notifyDataSetChanged()
    }

}