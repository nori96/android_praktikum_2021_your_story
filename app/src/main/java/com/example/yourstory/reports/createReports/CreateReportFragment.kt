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
import com.github.mikephil.charting.utils.ColorTemplate
import com.savvi.rangedatepicker.CalendarPickerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*
import java.text.SimpleDateFormat
import java.util.Date;
import android.widget.Toast
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


class CreateReportFragment : Fragment() {

    private lateinit var viewModel: CreateReportViewModel
    private lateinit var binding: CreateReportFragmentBinding
    private lateinit var hostFragmentNavController: NavController
    private lateinit var selectDatesAlertDialogBuilder: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = CreateReportFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CreateReportViewModel::class.java]
        if (container != null) {
            hostFragmentNavController = container.findNavController()
        }
        selectDatesAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        binding.createReportSelectCalendar.setOnClickListener {
            viewModel.tabSelected.value = 0
        }
        binding.createReportPieChart.setOnClickListener {
            setDateRanges()
            if (areDatesProperlySet()) {
                viewModel.tabSelected.value = 1
            } else {
                showDatesNotSelectedReport()
            }
        }
        binding.createReportBarChart.setOnClickListener {
            setDateRanges()
            if (areDatesProperlySet()) {
                viewModel.tabSelected.value = 2
            } else {
                showDatesNotSelectedReport()
            }
        }
        binding.createReportsExport.setOnClickListener {
            if (areDatesProperlySet()) {
                setDateRanges()
                viewModel.tabSelected.value = 3
            } else {
                showDatesNotSelectedReport()
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

        setupJoyButton()
        setupAngerButton()
        setupSurpriseButton()
        setupSadnessButton()
        setupDisgustButton()
        setupFearButton()

        // changes when setObservableArea() gets executed, a mechanism which we use to force redrawing all the time when changes occur
        viewModel.viewExposedStates.observe(viewLifecycleOwner, { newData ->
            updateAverageData(newData)
            setPieChartData()
            setBarChartData()
            setConfirmPageData()
        })

        // set always correct tab
        viewModel.tabSelected.observe(viewLifecycleOwner, {
            when (viewModel.tabSelected.value) {
                0 -> goToDateSelectionPage()
                1 -> goToPieChartPage()
                2 -> goToBarChartPage()
                3 -> goToConfirmPage()
            }
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

        binding.createReportBarChartGraph.setFitBars(true)
        binding.createReportBarChartGraph.description.text = ""
        binding.createReportBarChartGraph.isClickable = false
        binding.createReportBarChartGraph.isDoubleTapToZoomEnabled = false
        binding.createReportBarChartGraph.axisLeft.axisMinimum = 0f
        binding.createReportBarChartGraph.axisLeft.setDrawTopYLabelEntry(true)
        binding.createReportBarChartGraph.axisRight.setDrawLabels(false)
        binding.createReportBarChartGraph.legend.isEnabled = false
        binding.createReportBarChartGraph.setDrawValueAboveBar(true)
        binding.createReportBarChartGraph.setMaxVisibleValueCount(0)
        binding.createReportBarChartGraph.isHighlightPerTapEnabled = false
        binding.createReportBarChartGraph.isHighlightPerDragEnabled = false
        binding.createReportBarChartGraph.axisRight.setDrawGridLines(false)
        binding.createReportBarChartGraph.axisLeft.setDrawGridLines(false)
        binding.createReportBarChartGraph.xAxis.setDrawGridLines(false)
        binding.createReportBarChartGraph.xAxis.disableGridDashedLine()
        binding.createReportBarChartGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM

        return binding.root
    }

    // Dates not selected properly is shown if the user is inside the date picker, and the user wants to perform an action
    // which requires properly selected dates and this is not the case
    private fun showDatesNotSelectedReport() {
        selectDatesAlertDialogBuilder.setTitle(R.string.create_report_select_dates_heading)
        selectDatesAlertDialogBuilder.setMessage(R.string.create_report_select_dates_main_text)
        selectDatesAlertDialogBuilder.setPositiveButton(R.string.create_report_confirm_dialog) { _, _ ->
        }
        selectDatesAlertDialogBuilder.show()
    }

    private fun goToDateSelectionPage () {
        resetSelection()
        binding.createReportSelectCalendar.cardElevation = 30f
        binding.createReportsDateContent.visibility = View.VISIBLE
        viewModel.firstSelectedDate.value = 0
        viewModel.lastSelectedDate.value = 0
    }

    private fun goToPieChartPage() {
        resetSelection()
        binding.createReportPieChart.cardElevation = 30f
        binding.createReportsPieChartContent.visibility = View.VISIBLE
    }

    private fun goToBarChartPage() {
        resetSelection()
        binding.createReportBarChart.cardElevation = 30f
        binding.createReportsBarChartContent.visibility = View.VISIBLE
    }

    private fun goToConfirmPage() {
        resetSelection()
        binding.createReportsExport.cardElevation = 30f
        binding.createReportsExportContent.visibility = View.VISIBLE
    }

    // following six methods setup the functionality of the different mood buttons in relationship to the view model
    private fun setupJoyButton() {
        binding.createReportPieJoyButton.setOnClickListener {
            viewModel.joySelected.value = !viewModel.joySelected.value!!
            viewModel.setObservableArea()
        }
        binding.createReportBarJoyButton.setOnClickListener {
            viewModel.joySelected.value = !viewModel.joySelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.joySelected.observe(viewLifecycleOwner, { joySelected ->
            if (joySelected) {
                binding.createReportJoyCheckmark.visibility = View.VISIBLE
                binding.createReportJoyBarCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportJoyCheckmark.visibility = View.INVISIBLE
                binding.createReportJoyBarCheckmark.visibility = View.INVISIBLE
            }
        })
    }

    private fun setupAngerButton() {
        binding.createReportPieAngerButton.setOnClickListener {
            viewModel.angerSelected.value = !viewModel.angerSelected.value!!
            viewModel.setObservableArea()
        }
        binding.createReportBarAngerButton.setOnClickListener {
            viewModel.angerSelected.value = !viewModel.angerSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.angerSelected.observe(viewLifecycleOwner, { angerSelected ->
            if (angerSelected) {
                binding.createReportAngerCheckmark.visibility = View.VISIBLE
                binding.createReportAngerBarCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportAngerCheckmark.visibility = View.INVISIBLE
                binding.createReportAngerBarCheckmark.visibility = View.INVISIBLE
            }
        })
    }

    private fun setupSurpriseButton() {
        binding.createReportPieSurpriseButton.setOnClickListener {
            viewModel.surpriseSelected.value = !viewModel.surpriseSelected.value!!
            viewModel.setObservableArea()
        }
        binding.createReportBarSurpriseButton.setOnClickListener {
            viewModel.surpriseSelected.value = !viewModel.surpriseSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.surpriseSelected.observe(viewLifecycleOwner, { surpriseSelected ->
            if (surpriseSelected) {
                binding.createReportSurpriseCheckmark.visibility = View.VISIBLE
                binding.createReportSurpriseBarCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportSurpriseCheckmark.visibility = View.INVISIBLE
                binding.createReportSurpriseBarCheckmark.visibility = View.INVISIBLE
            }
        })
    }

    private fun setupSadnessButton() {
        binding.createReportPieSadnessButton.setOnClickListener {
            viewModel.sadnessSelected.value = !viewModel.sadnessSelected.value!!
            viewModel.setObservableArea()
        }
        binding.createReportBarSadnessButton.setOnClickListener {
            viewModel.sadnessSelected.value = !viewModel.sadnessSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.sadnessSelected.observe(viewLifecycleOwner, { sadnessSelected ->
            if (sadnessSelected) {
                binding.createReportSadnessCheckmark.visibility = View.VISIBLE
                binding.createReportSadnessBarCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportSadnessCheckmark.visibility = View.INVISIBLE
                binding.createReportSadnessBarCheckmark.visibility = View.INVISIBLE
            }
        })
    }

    private fun setupDisgustButton() {
        binding.createReportPieDisgustButton.setOnClickListener {
            viewModel.disgustSelected.value = !viewModel.disgustSelected.value!!
            viewModel.setObservableArea()
        }
        binding.createReportBarDisgustButton.setOnClickListener {
            viewModel.disgustSelected.value = !viewModel.disgustSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.disgustSelected.observe(viewLifecycleOwner, { disgustSelected ->
            if (disgustSelected) {
                binding.createReportDisgustCheckmark.visibility = View.VISIBLE
                binding.createReportDisgustBarCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportDisgustCheckmark.visibility = View.INVISIBLE
                binding.createReportDisgustBarCheckmark.visibility = View.INVISIBLE
            }
        })
    }

    private fun setupFearButton() {
        binding.createReportPieFearButton.setOnClickListener {
            viewModel.fearSelected.value = !viewModel.fearSelected.value!!
            viewModel.setObservableArea()
        }
        binding.createReportBarFearButton.setOnClickListener {
            viewModel.fearSelected.value = !viewModel.fearSelected.value!!
            viewModel.setObservableArea()
        }
        viewModel.fearSelected.observe(viewLifecycleOwner, { fearSelected ->
            if (fearSelected) {
                binding.createReportFearCheckmark.visibility = View.VISIBLE
                binding.createReportFearBarCheckmark.visibility = View.VISIBLE
            } else {
                binding.createReportFearCheckmark.visibility = View.INVISIBLE
                binding.createReportFearBarCheckmark.visibility = View.INVISIBLE
            }
        })
    }



    // used for resetting state before a state switch, just a helper function
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
            //Log.i("asdf",binding.createReportsCalendarRangePicker.selectedDates[0].toString())

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

    // checks inside the calendar view if correct dates are set
    private fun datesAreSelected(): Boolean {
        if ((binding.createReportsCalendarRangePicker.selectedDates.size >= 2 &&
                    binding.createReportsDateContent.visibility == View.VISIBLE)) {
            return true
        }
        return false
    }

    // checks if the view models dates are valid
    private fun  areDatesProperlySet(): Boolean {
        if (viewModel.firstSelectedDate.value!! > 0 && viewModel.lastSelectedDate.value!! > 0) {
            return true
        }
        return false
    }

    private fun setBarChartData() {
        val barEntries: ArrayList<BarEntry> = arrayListOf()
        val colors = arrayListOf<Int>()
        val labels = arrayListOf<String>()
        var drawIndex = 0f

        if (viewModel.joyAverage.value!! > 0 && viewModel.joySelected.value!!) {
            barEntries.add(BarEntry(drawIndex, viewModel.joyAverage.value!!))
            colors.add(ColorTemplate.rgb("#4BF430"))
            labels.add(resources.getString(R.string.create_report_joy_abbreviation))
            drawIndex += 1
        }
        if (viewModel.angerAverage.value!! > 0 && viewModel.angerSelected.value!!) {
            barEntries.add(BarEntry(drawIndex, viewModel.angerAverage.value!!))
            colors.add(ColorTemplate.rgb("#FF8181"))
            labels.add(resources.getString(R.string.create_report_anger_abbreviation))
            Log.i("asdf",resources.getString(R.string.create_report_anger_abbreviation))
            drawIndex += 1
        }
        if (viewModel.surpriseAverage.value!! > 0 && viewModel.surpriseSelected.value!!) {
            barEntries.add(BarEntry(drawIndex, viewModel.surpriseAverage.value!!))
            colors.add(ColorTemplate.rgb("#FEF63B"))
            labels.add(resources.getString(R.string.create_report_surprise_abbreviation))
            drawIndex += 1
        }
        if (viewModel.sadnessAverage.value!! > 0 && viewModel.sadnessSelected.value!!) {
            barEntries.add(BarEntry(drawIndex, viewModel.sadnessAverage.value!!))
            colors.add(ColorTemplate.rgb("#7BB8FF"))
            labels.add(resources.getString(R.string.create_report_sadness_abbreviation))
            drawIndex += 1
        }
        if (viewModel.disgustAverage.value!! > 0 && viewModel.disgustSelected.value!!) {
            barEntries.add(BarEntry(drawIndex, viewModel.disgustAverage.value!!))
            colors.add(ColorTemplate.rgb("#869200"))
            labels.add(resources.getString(R.string.create_report_disgust_abbreviation))
            drawIndex += 1
        }
        if (viewModel.fearAverage.value!! > 0 && viewModel.fearSelected.value!!) {
            barEntries.add(BarEntry(drawIndex, viewModel.fearAverage.value!!))
            colors.add(ColorTemplate.rgb("#B960FF"))
            labels.add(resources.getString(R.string.create_report_fear_abbreviation))
            drawIndex += 1
        }
        binding.createReportBarChartGraph.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.createReportBarChartGraph.xAxis.setLabelCount(drawIndex.toInt(), false)
        binding.createReportBarChartGraph.xAxis.axisMaximum = drawIndex
        val barDataSet = BarDataSet(barEntries,"")
        barDataSet.colors = colors
        val barData = BarData()
        barData.addDataSet(barDataSet)
        binding.createReportBarChartGraph.data = barData
        binding.createReportBarChartGraph.animateY(1000, Easing.EaseOutBack)
        binding.createReportBarChartGraph.invalidate()
    }

    private fun setPieChartData() {
        // default pie chart with no data, can happen if there are no emotional states in selected interval
        if ((viewModel.angerAverage.value == 0F || !viewModel.angerSelected.value!!) &&
            (viewModel.disgustAverage.value == 0F || !viewModel.disgustSelected.value!!) &&
            (viewModel.fearAverage.value == 0F || !viewModel.fearSelected.value!!) &&
            (viewModel.joyAverage.value == 0F || !viewModel.joySelected.value!!) &&
            (viewModel.sadnessAverage.value == 0F || !viewModel.sadnessSelected.value!!) &&
            (viewModel.surpriseAverage.value == 0F || !viewModel.surpriseSelected.value!!)) {
            val pieEntries = arrayListOf<PieEntry>()
            val typeAmountMap = HashMap<String,Float>()
            typeAmountMap.put("Empty",1F)
            val colors = arrayListOf(ColorTemplate.rgb("#D3D3D3"))
            for((name,average) in typeAmountMap) {
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
            if (viewModel.joyAverage.value!! > 0 && viewModel.joySelected.value!!) {
                pieEntries.add(PieEntry(viewModel.joyAverage.value!!, "Joy"))
                colors.add(ColorTemplate.rgb("#4BF430"))
            }
            if (viewModel.angerAverage.value!! > 0 && viewModel.angerSelected.value!!) {
                pieEntries.add(PieEntry(viewModel.angerAverage.value!!, "Anger"))
                colors.add(ColorTemplate.rgb("#FF8181"))
            }
            if (viewModel.surpriseAverage.value!! > 0 && viewModel.surpriseSelected.value!!) {
                pieEntries.add(PieEntry(viewModel.surpriseAverage.value!!, "Surprise"))
                colors.add(ColorTemplate.rgb("#FEF63B"))
            }
            if (viewModel.sadnessAverage.value!! > 0 && viewModel.sadnessSelected.value!!) {
                pieEntries.add(PieEntry(viewModel.sadnessAverage.value!!, "Sadness"))
                colors.add(ColorTemplate.rgb("#7BB8FF"))
            }
            if (viewModel.disgustAverage.value!! > 0 && viewModel.disgustSelected.value!!) {
                pieEntries.add(PieEntry(viewModel.disgustAverage.value!!, "Disgust"))
                colors.add(ColorTemplate.rgb("#869200"))
            }
            if (viewModel.fearAverage.value!! > 0 && viewModel.fearSelected.value!!) {
                pieEntries.add(PieEntry(viewModel.fearAverage.value!!, "Fear"))
                colors.add(ColorTemplate.rgb("#B960FF"))
            }
            val pieDataSet = PieDataSet(pieEntries, "")
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

    private fun setConfirmPageData() {
        binding.createReportFirstConfirmLabel.text = ""
        binding.createReportFirstConfirmValue.text = ""
        binding.createReportSecondConfirmLabel.text = ""
        binding.createReportSecondConfirmValue.text = ""
        binding.createReportThirdConfirmLabel.text = ""
        binding.createReportThirdConfirmValue.text = ""
        binding.createReportFourthConfirmLabel.text = ""
        binding.createReportFourthConfirmValue.text = ""
        binding.createReportFifthConfirmLabel.text = ""
        binding.createReportFifthConfirmValue.text = ""
        binding.createReportSixthConfirmLabel.text = ""
        binding.createReportSixthConfirmValue.text = ""

        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        val time = fmt.print(DateEpochConverter.convertEpochToDateTime(viewModel.firstSelectedDate.value!!))+
                " - " +
                fmt.print(DateEpochConverter.convertEpochToDateTime(viewModel.lastSelectedDate.value!!))
        binding.createReportDateConfirmValue.text = time

        var counter = 0
        if (viewModel.joyAverage.value!! > 0 && viewModel.joySelected.value!!) {
            setConfirmPageDataInCorrespondingColumn(counter, viewModel.joyAverage.value!!, resources.getString(R.string.likert_dialog_joy))
            counter += 1
        }
        if (viewModel.angerAverage.value!! > 0 && viewModel.angerSelected.value!!) {
            setConfirmPageDataInCorrespondingColumn(counter, viewModel.angerAverage.value!!, resources.getString(R.string.likert_dialog_anger))
            counter += 1
        }
        if (viewModel.surpriseAverage.value!! > 0 && viewModel.surpriseSelected.value!!) {
            setConfirmPageDataInCorrespondingColumn(counter, viewModel.surpriseAverage.value!!, resources.getString(R.string.likert_dialog_surprise))
            counter += 1
        }
        if (viewModel.sadnessAverage.value!! > 0 && viewModel.sadnessSelected.value!!) {
            setConfirmPageDataInCorrespondingColumn(counter, viewModel.sadnessAverage.value!!, resources.getString(R.string.likert_dialog_sadness))
            counter += 1
        }
        if (viewModel.disgustAverage.value!! > 0 && viewModel.disgustSelected.value!!) {
            setConfirmPageDataInCorrespondingColumn(counter, viewModel.disgustAverage.value!!, resources.getString(R.string.likert_dialog_disgust))
            counter += 1
        }
        if (viewModel.fearAverage.value!! > 0 && viewModel.fearSelected.value!!) {
            setConfirmPageDataInCorrespondingColumn(counter, viewModel.fearAverage.value!!, resources.getString(R.string.likert_dialog_fear))
            counter += 1
        }
    }

    private fun setConfirmPageDataInCorrespondingColumn(index: Int, value: Float, label: String) {
        when (index) {
            0 -> {
                binding.createReportFirstConfirmLabel.text = label
                binding.createReportFirstConfirmValue.text = value.toString()
            }
            1 -> {
                binding.createReportSecondConfirmLabel.text = label
                binding.createReportSecondConfirmValue.text = value.toString()
            }
            2 -> {
                binding.createReportThirdConfirmLabel.text = label
                binding.createReportThirdConfirmValue.text = value.toString()
            }
            3 -> {
                binding.createReportFourthConfirmLabel.text = label
                binding.createReportFourthConfirmValue.text = value.toString()
            }
            4 -> {
                binding.createReportFifthConfirmLabel.text = label
                binding.createReportFifthConfirmValue.text = value.toString()
            }
            5 -> {
                binding.createReportSixthConfirmLabel.text = label
                binding.createReportSixthConfirmValue.text = value.toString()
            }
        }
    }

    private fun updateAverageData(data: List<EmotionalState>) {
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
    }
}