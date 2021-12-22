package com.example.yourstory.reports.createReports

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.yourstory.R
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.ReportEntry
import com.example.yourstory.databinding.CreateReportFragmentBinding
import com.example.yourstory.utils.DateEpochConverter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.savvi.rangedatepicker.CalendarPickerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*
import java.text.SimpleDateFormat
import java.util.Date;
class CreateReportFragment : Fragment() {

    private lateinit var viewModel: CreateReportViewModel
    private lateinit var binding: CreateReportFragmentBinding
    private lateinit var hostFragmentNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CreateReportFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CreateReportViewModel::class.java]
        if (container != null) {
            hostFragmentNavController = container.findNavController()
        }
        binding.createReportSelectCalendar.setOnClickListener {
            resetSelection()
            binding.createReportSelectCalendar.cardElevation = 30f
            binding.createReportsDateContent.visibility = View.VISIBLE
        }
        binding.createReportPieChart.setOnClickListener {
            setDateRanges()
            if (areDatesProperlySet()) {
                setDateRanges()
                resetSelection()
                binding.createReportPieChart.cardElevation = 30f
                binding.createReportsPieChartContent.visibility = View.VISIBLE
            }
        }
        binding.createReportBarChart.setOnClickListener {
            setDateRanges()
            if (areDatesProperlySet()) {
                setDateRanges()
                resetSelection()
                binding.createReportBarChart.cardElevation = 30f
                binding.createReportsBarChartContent.visibility = View.VISIBLE
            }
        }
        binding.createReportsExport.setOnClickListener {
            setDateRanges()
            if (areDatesProperlySet()) {
                setDateRanges()
                resetSelection()
                binding.createReportsExport.cardElevation = 30f
                binding.createReportsExportContent.visibility = View.VISIBLE
            }
        }

        // date means day here...
        val nextYear: Calendar = Calendar.getInstance();
        nextYear.add(Calendar.DATE, 1)

        val lastYear: Calendar  = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, - 5)
        binding.createReportsCalendarRangePicker.init(
            lastYear.time,
            nextYear.time,
            SimpleDateFormat("MMMM, yyyy", Locale.GERMANY))
            .inMode(CalendarPickerView.SelectionMode.RANGE)
            .withSelectedDate(Date())

        binding.createReportPieJoyButton.setOnClickListener {
            viewModel.joySelected.value = !viewModel.joySelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.joySelected.observe(viewLifecycleOwner, { joySelected ->
            if (joySelected) {
                binding.createReportJoyCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportJoyCheckmark.visibility = View.INVISIBLE
            }

        })

        binding.createReportPieAngerButton.setOnClickListener {
            viewModel.angerSelected.value = !viewModel.angerSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.angerSelected.observe(viewLifecycleOwner, { angerSelected ->
            if (angerSelected) {
                binding.createReportAngerCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportAngerCheckmark.visibility = View.INVISIBLE
            }
        })

        binding.createReportPieSurpriseButton.setOnClickListener {
            viewModel.surpriseSelected.value = !viewModel.surpriseSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.surpriseSelected.observe(viewLifecycleOwner, { surpriseSelected ->
            if (surpriseSelected) {
                binding.createReportSurpriseCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportSurpriseCheckmark.visibility = View.INVISIBLE
            }

        })

        binding.createReportPieSadnessButton.setOnClickListener {
            viewModel.sadnessSelected.value = !viewModel.sadnessSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.sadnessSelected.observe(viewLifecycleOwner, { sadnessSelected ->
            if (sadnessSelected) {
                binding.createReportSadnessCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportSadnessCheckmark.visibility = View.INVISIBLE
            }

        })

        binding.createReportPieDisgustButton.setOnClickListener {
            viewModel.disgustSelected.value = !viewModel.disgustSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.disgustSelected.observe(viewLifecycleOwner, { disgustSelected ->
            if (disgustSelected) {
                binding.createReportDisgustCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportDisgustCheckmark.visibility = View.INVISIBLE
            }

        })

        binding.createReportPieFearButton.setOnClickListener {
            viewModel.fearSelected.value = !viewModel.fearSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.fearSelected.observe(viewLifecycleOwner, { fearSelected ->
            if (fearSelected) {
                binding.createReportFearCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportFearCheckmark.visibility = View.INVISIBLE
            }
        })

        // changes when setObservableArea() gets executed, a mechanism which we use to force redrawing all the time
        viewModel.viewExposedStates.observe(viewLifecycleOwner, { newData ->
            setPieChartData(newData)
        })

        viewModel.lastSelectedDate.observe(viewLifecycleOwner, {
            viewModel.setObservableArea()
        })
        binding.cancelCreateReportDialog.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_createReportFragment_to_navigation_reports)
        }
        binding.confirmCreateReportDialog.setOnClickListener {
            viewModel.insertCurrentReport()
            hostFragmentNavController.navigate(R.id.action_createReportFragment_to_navigation_reports)
        }

        return binding.root
    }

    private fun resetSelection() {
        binding.createReportSelectCalendar.cardElevation = 0f
        binding.createReportPieChart.cardElevation = 0f
        binding.createReportBarChart.cardElevation = 0f
        binding.createReportsExport.cardElevation = 0f
        binding.createReportsDateContent.visibility = View.INVISIBLE
        binding.createReportsPieChartContent.visibility = View.INVISIBLE
        binding.createReportsBarChartContent.visibility = View.INVISIBLE
        binding.createReportsExportContent.visibility = View.INVISIBLE
    }

    private fun setDateRanges() {
        if (datesAreSelected()) {
            Log.i("asdf",binding.createReportsCalendarRangePicker.selectedDates[0].toString())

            viewModel.firstSelectedDate.value = DateEpochConverter.convertDateTimeToEpoch (
                DateTime(binding.createReportsCalendarRangePicker.selectedDates[0])
                    .withTime(0, 0, 0, 0).toDateTimeISO().toString()
            )
            /*Log.i("asdf", binding.createReportsCalendarRangePicker.selectedDates.last().toString())
            Log.i("asdf",DateTime(binding.createReportsCalendarRangePicker.selectedDates.last(),
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+1")))
                .withTime(23, 59, 59, 999).toDateTimeISO().toString())*/
            //TODO get local time zone programmatically, and probable bug in other usages of this kind
            viewModel.lastSelectedDate.value = DateEpochConverter.convertDateTimeToEpoch (
                DateTime(binding.createReportsCalendarRangePicker.selectedDates.last(),
                    DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+1")))
                    .withTime(23, 59, 59, 999).toDateTimeISO().toString()
            )
            //Log.i("asdf", DateEpochConverter.convertEpochToDateTime(viewModel.lastSelectedDate.value!!).toString())
        }
    }

    private fun datesAreSelected(): Boolean {
        if ((binding.createReportsCalendarRangePicker.selectedDates.size >= 2 &&
                    binding.createReportsDateContent.visibility == View.VISIBLE)) {
            return true
        }
        return false
    }
    private fun  areDatesProperlySet(): Boolean {
        if (viewModel.firstSelectedDate.value!! > 0 && viewModel.lastSelectedDate.value!! > 0) {
            return true
        }
        return false
    }

    private fun setPieChartData(data: List<EmotionalState>) {
        var joyAverage = 0F
        var angerAverage = 0F
        var surpriseAverage = 0F
        var sadnessAverage = 0F
        var disgustAverage = 0F
        var fearAverage = 0F

        // averaging values
        for (emotionalState in data) {
            joyAverage += emotionalState.joy
            angerAverage += emotionalState.anger
            surpriseAverage += emotionalState.surprise
            sadnessAverage += emotionalState.sadness
            disgustAverage += emotionalState.disgust
            fearAverage += emotionalState.fear
        }
        if (data.isNotEmpty()) {
            joyAverage /= data.size
            angerAverage /= data.size
            surpriseAverage /= data.size
            sadnessAverage /= data.size
            disgustAverage /= data.size
            fearAverage /= data.size
        }

        viewModel.joyAverage.value = joyAverage
        viewModel.angerAverage.value = angerAverage
        viewModel.surpriseAverage.value = surpriseAverage
        viewModel.sadnessAverage.value = sadnessAverage
        viewModel.disgustAverage.value = disgustAverage
        viewModel.fearAverage.value = fearAverage

        if ((angerAverage == 0F || !viewModel.angerSelected.value!!) &&
            (disgustAverage == 0F || !viewModel.disgustSelected.value!!) &&
            (fearAverage == 0F || !viewModel.fearSelected.value!!) &&
            (joyAverage == 0F || !viewModel.joySelected.value!!) &&
            (sadnessAverage == 0F || !viewModel.sadnessSelected.value!!) &&
            (surpriseAverage == 0F || !viewModel.surpriseSelected.value!!)) {
            // default pie chart with no data, can happen if there are no emotional states in selected interval
            val pieEntries = arrayListOf<PieEntry>()
            val typeAmountMap = HashMap<String,Float>()
            typeAmountMap.put("Empty",1F)
            var colors = arrayListOf<Int>(ColorTemplate.rgb("#D3D3D3"))
            for((name,average) in typeAmountMap){
                pieEntries.add(PieEntry(average,name))
            }
            val pieDataSet = PieDataSet(pieEntries,"Empty")
            pieDataSet.colors = colors
            pieDataSet.setDrawValues(false)

            binding.createReportPieChartGraph.data = PieData(pieDataSet)
            binding.createReportPieChartGraph.description.isEnabled = false
            binding.createReportPieChartGraph.setDrawEntryLabels(false)
            binding.createReportPieChartGraph.legend.isEnabled = false
            binding.createReportPieChartGraph.isDrawHoleEnabled = false

        } else {
            val pieEntries = arrayListOf<PieEntry>()
            val colors = arrayListOf<Int>()
            if (joyAverage > 0 && viewModel.joySelected.value!!) {
                pieEntries.add(PieEntry(joyAverage, "Joy"))
                colors.add(ColorTemplate.rgb("#4BF430"))
            }
            if (angerAverage > 0 && viewModel.angerSelected.value!!) {
                pieEntries.add(PieEntry(angerAverage, "Anger"))
                colors.add(ColorTemplate.rgb("#FF8181"))
            }
            if (surpriseAverage > 0 && viewModel.surpriseSelected.value!!) {
                pieEntries.add(PieEntry(surpriseAverage, "Surprise"))
                colors.add(ColorTemplate.rgb("#FEF63B"))
            }
            if (sadnessAverage > 0 && viewModel.sadnessSelected.value!!) {
                pieEntries.add(PieEntry(sadnessAverage, "Sadness"))
                colors.add(ColorTemplate.rgb("#7BB8FF"))
            }
            if (disgustAverage > 0 && viewModel.disgustSelected.value!!) {
                pieEntries.add(PieEntry(disgustAverage, "Disgust"))
                colors.add(ColorTemplate.rgb("#869200"))
            }
            if (fearAverage > 0 && viewModel.fearSelected.value!!) {
                pieEntries.add(PieEntry(fearAverage, "Fear"))
                colors.add(ColorTemplate.rgb("#B960FF"))
            }
            val pieDataSet = PieDataSet(pieEntries, "Emotions")
            pieDataSet.valueTextSize = 20f;
            pieDataSet.colors = colors
            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(false)
            binding.createReportPieChartGraph.data = pieData
            binding.createReportPieChartGraph.setDrawEntryLabels(true)
            binding.createReportPieChartGraph.legend.isEnabled = false
            binding.createReportPieChartGraph.setEntryLabelColor(R.color.black)
            binding.createReportPieChartGraph.description.isEnabled = false
            binding.createReportPieChartGraph.isDrawHoleEnabled = true
        }
        binding.createReportPieChartGraph.invalidate()
        binding.createReportPieChartGraph.animateX(1000, Easing.EaseOutBack)
    }
}