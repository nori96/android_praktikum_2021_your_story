package com.example.yourstory.reports.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.databinding.ReportRowItemBinding
import com.example.yourstory.reports.ReportModel

class ReportsAdapter() : RecyclerView.Adapter<ReportsAdapter.ViewHolder>(){

    var dataSet: ArrayList<ReportModel> = arrayListOf()
    private lateinit var binding: ReportRowItemBinding

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(reportView: View) : RecyclerView.ViewHolder(reportView) {
        val textViewDate: TextView
        val textViewAverage: TextView
        val textViewTimeInterval: TextView

        init {
            textViewDate = reportView.findViewById(R.id.report_item_date)
            textViewAverage = reportView.findViewById(R.id.report_item_MS)
            textViewTimeInterval = reportView.findViewById(R.id.report_item_timeinterval)
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
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}