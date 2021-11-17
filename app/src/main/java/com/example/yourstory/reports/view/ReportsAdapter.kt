package com.example.yourstory.reports.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.databinding.ReportRowItemBinding
import com.example.yourstory.reports.ReportModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlin.math.absoluteValue

class ReportsAdapter() : RecyclerView.Adapter<ReportsAdapter.ViewHolder>(){

    var dataSet: ArrayList<ReportModel> = arrayListOf()

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(reportView: View) : RecyclerView.ViewHolder(reportView) {
       val textViewDate: TextView
       val textViewAverage: TextView
        val textViewTimeInterval: TextView
        val barChart: BarChart

        init {
            textViewDate = reportView.findViewById(R.id.report_item_date)
            textViewAverage = reportView.findViewById(R.id.report_item_MS)
            textViewTimeInterval = reportView.findViewById(R.id.report_item_timeinterval)
            barChart = reportView.findViewById(R.id.barChart)
            barChart.setFitBars(true)
            barChart.isAutoScaleMinMaxEnabled
            barChart.description.text = ""
            barChart.xAxis.isEnabled = false
            barChart.isClickable = false
            barChart.setDrawGridBackground(false)
            barChart.isDoubleTapToZoomEnabled = false
            barChart.axisLeft.setDrawLabels(false)
            barChart.axisRight.setDrawLabels(false)
            barChart.legend.isEnabled = false
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ReportsAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        val reportView = inflater.inflate(R.layout.report_row_item, viewGroup, false)
        return ViewHolder(reportView)
    }

    override fun onBindViewHolder(viewHolder: ReportsAdapter.ViewHolder, position: Int) {
        viewHolder.textViewDate.text = dataSet[position].date
        viewHolder.textViewAverage.text = dataSet[position].average.toString()
        viewHolder.textViewTimeInterval.text = dataSet[position].interval
        var barEntries: ArrayList<BarEntry> = arrayListOf()

        barEntries.add(BarEntry(0f,dataSet[position].joyAverage))

        barEntries.add(BarEntry(1f,dataSet[position].angerAverage))

        barEntries.add(BarEntry(2f,dataSet[position].supriseAverage))

        barEntries.add(BarEntry(3f,dataSet[position].sadnessAverage))

        barEntries.add(BarEntry(4f,dataSet[position].disgustAverage))

        barEntries.add(BarEntry(5f,dataSet[position].fearAverage))

        var barDataSet = BarDataSet(barEntries, "")
        barDataSet.colors = listOf(ColorTemplate.rgb("##4BF430"),ColorTemplate.rgb("#FF8181"),ColorTemplate.rgb("#FEF63B"),ColorTemplate.rgb("#7BB8FF"),ColorTemplate.rgb("#869200"),ColorTemplate.rgb("#B960FF"))
        var barData = BarData()
        barData.addDataSet(barDataSet)
        viewHolder.barChart.data = barData
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}