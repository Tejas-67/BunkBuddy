package com.example.bunkbuddy.util

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bunkbuddy.UI.SubjectViewModel
import com.example.bunkbuddy.fragments.TimetableContentFragment

class ViewPagerAdapter(fa: FragmentActivity, private val viewModel: SubjectViewModel): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> TimetableContentFragment(viewModel.monday)
            1 -> TimetableContentFragment(viewModel.tuesday)
            2 -> TimetableContentFragment(viewModel.wednesday)
            3 -> TimetableContentFragment(viewModel.thursday)
            4 -> TimetableContentFragment(viewModel.friday)
            5 -> TimetableContentFragment(viewModel.saturday)
            else -> TimetableContentFragment(viewModel.sunday)
        }
    }
}