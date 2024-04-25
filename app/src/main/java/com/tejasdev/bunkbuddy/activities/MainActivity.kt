package com.tejasdev.bunkbuddy.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.backup.BackupWorker
import com.tejasdev.bunkbuddy.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get()=_binding!!
    private lateinit var navController: NavController
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    @Inject lateinit var workManager: WorkManager
    @Inject lateinit var authViewModel: AuthViewModel
    @Inject lateinit var viewModel: SubjectViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        sharedPref = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        setUpTheme()
        makeBackupDataDecision()
        navController.addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id){
                R.id.allSubjectsFragment, R.id.timetableFragment, R.id.profileFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }


    private fun makeBackupDataDecision(){
        Log.w("backup", authViewModel.isBackedUpDataFetched().toString())
        if(authViewModel.isAutomaticBackupOn() && authViewModel.isBackedUpDataFetched()){
            scheduleBackup()
        }
        else if(!authViewModel.ifDataRestoreAlertShown()){
            authViewModel.markDataRestoreAlertShown()
            startDataRestore()
        }
    }

    private fun startDataRestore() {
        Log.w("backup", "showing dialog")
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Restore Previous Data")
            .setMessage("Would you like to restore you previous data from our servers?\n\nIf you prefer to restore later, you can go to settings and manually restore the data.")
            .setPositiveButton("Restore") {dialog, which ->
                restoreData()
                dialog.dismiss()
            }
            .setNegativeButton("Later") {dialog, which->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
        dialog.show()
    }

    private fun restoreData(){
        authViewModel.fetchData(authViewModel.getEmail()){ success, data ->
            if(success){
                Log.w("backup", data.toString())
                authViewModel.markBackupDataFetched()
                for(subject in data!!){
                    viewModel.addSubject(subject)
                }
                showSnackbar("Data restored successfully")
            }else{
                showSnackbar("Something went wrong")
            }
        }
    }

    private fun showSnackbar(message: String){
        val root = findViewById<View>(android.R.id.content)
        Snackbar.make(root, message, 2000).show()
    }
    private fun scheduleBackup() {
        if(!authViewModel.isAutomaticBackupOn()) return
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val repeatInterval = 1
        val workRequest = PeriodicWorkRequestBuilder<BackupWorker>(
            repeatInterval.toLong(), TimeUnit.DAYS
        )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            AUTOMATIC_BACKUP_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, Observer { workInfo->
                if(workInfo!=null && workInfo.state == WorkInfo.State.SUCCEEDED){
                    Log.w("workmanager-backup", "upload done")
                }
            })
    }

    override fun onBackPressed() {
        val currentDestination = navController.currentDestination?.id
        when(currentDestination){
            R.id.settingsFragment -> navController.popBackStack(R.id.profileFragment, false)
            else -> super.onBackPressed()
        }
    }

    private fun showBottomNav(){
        binding.bottomNav.visibility = View.VISIBLE
    }
    private fun hideBottomNav(){
        binding.bottomNav.visibility = View.GONE
    }
    private fun setUpTheme() {
        if(sharedPref.getBoolean(DARK_THEME, true))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
    companion object{
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
        const val PRIVACY_POLICY_LINK = "https://bunkbuddyprivacypolicy.blogspot.com/2023/12/privacy-policy-for-bunkbuddy.html"
        const val SHARED_PREF = "BunkBuddySharedPref"
        const val DARK_THEME = "dark_theme"
        const val NOTIFICATION_ENABLED = "notification_enabled"
        const val AUTOMATIC_BACKUP_WORK = "automatic_backup_work"
    }
}