package com.tejasdev.bunkbuddy.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tejasdev.bunkbuddy.App
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.databinding.FragmentStatisticsBinding
import com.tejasdev.bunkbuddy.datamodel.Subject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubjectViewModel by viewModels()
    private val pieChartData = MutableLiveData<List<PieEntry>>(listOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalAttendedClasses.observe(viewLifecycleOwner, Observer {
            binding.totalAttended.text = "Total Attended: $it"
        })
        viewModel.totalMissedClasses.observe(viewLifecycleOwner, Observer {
            binding.totalMissed.text = "Total Missed: $it"
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.overallAttendancePercentage.collectLatest { attendancePercent->
                binding.overallAttendancePercTv.text = "$attendancePercent%"
                binding.overallAttendanceProgress.setProgress(attendancePercent, true)
            }
        }
        pieChartData.observe(viewLifecycleOwner, Observer {
            updatePieChart(it)
        })

        viewModel.savedSubjects.observe(viewLifecycleOwner, Observer{
            pieChartData.postValue(
                calculateContribution(it)
            )
        })
    }

    private fun updatePieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.color
        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.legend.textColor =
            if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES)
                Color.WHITE
            else
                Color.BLACK
        binding.pieChart.holeRadius = 0f
        binding.pieChart.transparentCircleRadius = 0f
        binding.pieChart.invalidate()
    }

    private fun calculateContribution(subjects: List<Subject>): List<PieEntry>{
        val entries = mutableListOf<PieEntry>()
        val totalClasses = subjects.sumOf { it.attended + it.missed }
        subjects.forEach{subject ->
            val perc = (subject.attended.toFloat()/totalClasses.toFloat())*100
            entries.add(PieEntry(perc, subject.name))
        }
        return entries
    }
}