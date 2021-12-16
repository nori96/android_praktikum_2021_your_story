package com.example.yourstory.reports

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.database.data.Entry
import com.example.yourstory.database.data.ReportEntry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class ReportsAdapter() : RecyclerView.Adapter<ReportsAdapter.ViewHolder>(){

    var dataSet: List<ReportEntry> = listOf()
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
            barChart.isDoubleTapToZoomEnabled = false
            barChart.axisLeft.setDrawLabels(false)
            barChart.axisRight.setDrawLabels(false)
            barChart.legend.isEnabled = false
            barChart.setDrawValueAboveBar(true)
            barChart.setMaxVisibleValueCount(0)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        val reportView = inflater.inflate(R.layout.report_row_item, viewGroup, false)
        return ViewHolder(reportView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        viewHolder.textViewDate.text = fmt.print(dataSet[position].madeAt * 1000)
        viewHolder.textViewAverage.text = dataSet[position].averageEmotionality.toString()
        viewHolder.textViewTimeInterval.text = dataSet[position].period
        var barEntries: ArrayList<BarEntry> = arrayListOf()

        barEntries.add(BarEntry(0f,dataSet[position].averageJoy))

        barEntries.add(BarEntry(1f,dataSet[position].averageAnger))

        barEntries.add(BarEntry(2f,dataSet[position].averageSurprise))

        barEntries.add(BarEntry(3f,dataSet[position].averageSadness))

        barEntries.add(BarEntry(4f,dataSet[position].averageDisgust))

        barEntries.add(BarEntry(5f,dataSet[position].averageFear))

        var barDataSet = BarDataSet(barEntries, "")
        barDataSet.colors = listOf(ColorTemplate.rgb("##4BF430"),ColorTemplate.rgb("#FF8181"),ColorTemplate.rgb("#FEF63B"),ColorTemplate.rgb("#7BB8FF"),ColorTemplate.rgb("#869200"),ColorTemplate.rgb("#B960FF"))
        var barData = BarData()
        barData.addDataSet(barDataSet)
        viewHolder.barChart.data = barData
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
    fun setData(entries: List<ReportEntry>) {
        this.dataSet = (entries.sortedBy { it.madeAt }).reversed()
    notifyDataSetChanged()
    }
}