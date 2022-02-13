package com.example.yourstory.reports

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.MainActivity
import com.example.yourstory.R
import com.example.yourstory.database.data.ReportEntry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.report_row_item.view.*
import kotlinx.android.synthetic.main.text_entry_diary_layout.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ReportsAdapter(var lifeCycleOwner: LifecycleOwner) : RecyclerView.Adapter<ReportsAdapter.ViewHolder>(){

    private lateinit var selectedItems: ArrayList<Int>
    private var dataSet: List<ReportEntry> = listOf()
    lateinit var viewModel: ReportsViewModel
    private lateinit var context: Context

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
            barChart.description.text = ""

            barChart.isClickable = false
            barChart.isDoubleTapToZoomEnabled = false

            barChart.axisLeft.setDrawTopYLabelEntry(true)
            barChart.axisLeft.setLabelCount(3, false)
            barChart.axisRight.setDrawLabels(false)
            barChart.legend.isEnabled = false
            barChart.axisLeft.axisMinimum = 0f
            barChart.setDrawValueAboveBar(true)
            barChart.setMaxVisibleValueCount(0)
            barChart.isHighlightPerTapEnabled = false
            barChart.isHighlightPerDragEnabled = false
            barChart.axisRight.setDrawGridLines(false)
            barChart.axisLeft.setDrawGridLines(false)
            barChart.xAxis.setDrawGridLines(false)
            barChart.xAxis.disableGridDashedLine()
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            if (Locale.getDefault().language == "en") {
                val xAxisLabels = listOf("J", "A", "S", "S", "D", "F")
                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            } else {
                val xAxisLabels = listOf("F", "W", "Ü", "T", "E", "A")
                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        viewModel = ViewModelProvider(context as MainActivity)[ReportsViewModel::class.java]

        viewModel.selectedItems.observe(lifeCycleOwner,{
            selectedItems = ArrayList(it)
        })

        val reportView = inflater.inflate(R.layout.report_row_item, viewGroup, false)
        return ViewHolder(reportView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        viewHolder.textViewDate.text = fmt.print(dataSet[position].madeAt * 1000)
        viewHolder.textViewAverage.text = dataSet[position].averageEmotionality.toString()
        viewHolder.textViewTimeInterval.text = dataSet[position].period

        val barEntries: ArrayList<BarEntry> = arrayListOf()
        barEntries.add(BarEntry(0f,dataSet[position].averageJoy))
        barEntries.add(BarEntry(1f,dataSet[position].averageAnger))
        barEntries.add(BarEntry(2f,dataSet[position].averageSurprise))
        barEntries.add(BarEntry(3f,dataSet[position].averageSadness))
        barEntries.add(BarEntry(4f,dataSet[position].averageDisgust))
        barEntries.add(BarEntry(5f,dataSet[position].averageFear))

        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.colors = listOf(
            ColorTemplate.rgb("##4BF430"),
            ColorTemplate.rgb("#FF8181"),
            ColorTemplate.rgb("#FEF63B"),
            ColorTemplate.rgb("#7BB8FF"),
            ColorTemplate.rgb("#869200"),
            ColorTemplate.rgb("#B960FF")
        )
        val barData = BarData()
        barData.addDataSet(barDataSet)
        viewHolder.barChart.data = barData


        if(!selectedItems.contains(position)){
            viewHolder.itemView.report_item_delete.visibility = View.INVISIBLE
            viewHolder.itemView.cardView_reports.setCardBackgroundColor(ContextCompat.getColor(context, R.color.egg_white))
        }else if (selectedItems.contains(position)) {
            viewHolder.itemView.report_item_delete.visibility = View.VISIBLE
            viewHolder.itemView.cardView_reports.setCardBackgroundColor(Color.LTGRAY)
        }


        viewHolder.itemView.setOnTouchListener(View.OnTouchListener {
                view, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                view.tag = true
            } else if (view.tag as Boolean) {
                val eventDuration = event.eventTime - event.downTime
                if (eventDuration > ViewConfiguration.getLongPressTimeout()) {
                    view.tag = false

                    if(selectedItems.contains(position)){
                        viewHolder.itemView.report_item_delete.visibility = View.INVISIBLE
                        viewHolder.itemView.cardView_reports.setCardBackgroundColor(ContextCompat.getColor(context, R.color.egg_white))
                        selectedItems.remove(position)
                        viewModel.selectedItems.postValue(selectedItems)
                    }else{
                        viewHolder.itemView.report_item_delete.visibility = View.VISIBLE
                        viewHolder.itemView.cardView_reports.setCardBackgroundColor(Color.LTGRAY)
                        selectedItems.add(position)
                        viewModel.selectedItems.postValue(selectedItems)
                    }

                    if(selectedItems.isNotEmpty()){
                        viewModel.deleteState.postValue(true)
                    }else{
                        viewModel.deleteState.postValue(false)
                    }
                    return@OnTouchListener true
                }
            }
            return@OnTouchListener true
        })
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
    fun setData(entries: List<ReportEntry>) {
        this.dataSet = (entries.sortedBy { it.madeAt }).reversed()
        notifyDataSetChanged()
    }

    fun getSelectedEntries(): ArrayList<ReportEntry>{
        if(selectedItems.isEmpty()){
            return arrayListOf()
        }
        val list = arrayListOf<ReportEntry>()
        for (int in selectedItems){
            list.add(dataSet[int])
        }
        return list
    }

    fun deleteSelectedEntries() {
        viewModel.selectedItems.postValue(listOf())
        viewModel.deleteState.postValue(false)
    }
}