package com.tejasdev.bunkbuddy.fragments

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AlarmViewModel
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.alarm.AlarmReceiver
import com.tejasdev.bunkbuddy.databinding.FragmentSettingsBinding
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    val viewModel: SubjectViewModel by viewModels()
    val alarmViewModel: AlarmViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var isNotificationEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireContext().getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        isNotificationEnabled = sharedPref.getBoolean(MainActivity.NOTIFICATION_ENABLED, false)
        binding.alertsSwitch.isChecked = isNotificationEnabled
        binding.themeSwitch.isChecked = sharedPref.getBoolean(MainActivity.DARK_THEME, true)
        binding.backupSwitch.isChecked = authViewModel.isAutomaticBackupOn()
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            changeTheme(isChecked)
        }
        binding.backupSwitch.setOnCheckedChangeListener { _, _ ->
            authViewModel.toggleAutomaticBackupState()
        }
        binding.alertsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) scheduleAlarms()
            else removeScheduledAlarms()
        }
        binding.backupSwitch.setOnCheckedChangeListener { _, _ ->
            backupInBackground()
        }
        binding.alertsSwitch.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked) scheduleAlarms()
            else removeScheduledAlarms()
        }
        binding.changePasswordLl.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
        }
        binding.restoreDataCard.setOnClickListener {
            authViewModel.fetchData(authViewModel.getEmail()){ success, data ->
                if(success){
                    data?.let{addDataToDatabase(data)}
                    showSnackbar("Data restored successfully")
                }
                else{
                    showSnackbar("Something went wrong")
                }
            }
        }
        authViewModel.dataRestoreServiceRunning.observe(viewLifecycleOwner, Observer {
            updateDataRestoreProgressBar(it)
        })
    }

    private fun addDataToDatabase(data: List<Subject>) {
        for(subject in data){
            viewModel.addSubject(subject)
        }
    }

    private fun showSnackbar(message: String){
        Snackbar.make(requireView(), message, 2000).show()
    }

    private fun updateDataRestoreProgressBar(isRestoring: Boolean){
        val state = if(isRestoring) View.VISIBLE else View.GONE
        binding.restoreDataProgress.visibility = state
    }
    private fun scheduleAlarms() {
        checkNotificationSettings()
        val lectures = viewModel.getAllLecturesSync()
        createNotificationChannel()
        for(lecture in lectures){
            val perc = getAttendancePerc(lecture)
            if(perc<lecture.subject.requirement){
                alarmViewModel.setAlarm(lecture)
            }
        }
        editor.putBoolean(MainActivity.NOTIFICATION_ENABLED, true)
        editor.apply()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                AlarmReceiver.NOTIFICATION_CHANNEL_ID,
                AlarmReceiver.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableVibration(true)
            val manager = requireContext().getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun changeTheme(isChecked: Boolean){
        if(isChecked){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        editor.putBoolean(MainActivity.DARK_THEME, isChecked)
        editor.apply()
    }
    private fun backupInBackground(){
        //start backup here
    }
    private fun removeScheduledAlarms() {
        editor.putBoolean(MainActivity.NOTIFICATION_ENABLED, false)
        editor.apply()
        val lectures = viewModel.getAllLecturesSync()
        changeNotificationSwitchState()
        for(lecture in lectures){
            alarmViewModel.cancelAlarm(lecture)
        }
    }
    private fun changeNotificationSwitchState() {
        isNotificationEnabled = !isNotificationEnabled
        editor.putBoolean(MainActivity.NOTIFICATION_ENABLED, isNotificationEnabled)
        binding.alertsSwitch.isChecked = isNotificationEnabled
        editor.apply()
    }
    private fun getAttendancePerc(lecture: Lecture): Double {
        return ((lecture.subject.attended.toDouble()).div(lecture.subject.attended.toDouble() + lecture.subject.missed.toDouble()))*100
    }
    private fun checkNotificationSettings(){
        if(!isNotificationPermissionGranted(requireContext())){
            requestNotificationPermission(requireActivity())
        }
    }

    private fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            activity.startActivityForResult(intent,
                MainActivity.NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivityForResult(intent,
                MainActivity.NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            binding.alertsSwitch.isClickable = isNotificationPermissionGranted(requireContext())
        }
    }
    private fun isNotificationPermissionGranted(context: Context): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        return notificationManager.areNotificationsEnabled()
    }
}