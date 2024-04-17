package com.tejasdev.bunkbuddy.activities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.alarm.AlarmReceiver
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AlarmViewModel
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.databinding.ActivityMainBinding
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.util.constants.ALERTS_OFF
import com.tejasdev.bunkbuddy.util.constants.ALERTS_ON
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get()=_binding!!
    private lateinit var navController: NavController
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
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
    }

    private fun setUpTheme() {
        if(sharedPref.getBoolean(DARK_THEME, true))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
    private fun showSnackbar(view: View, message: String){
        Snackbar.make(view, message, 1000).show()
    }
    companion object{
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
        const val PRIVACY_POLICY_LINK = "https://bunkbuddyprivacypolicy.blogspot.com/2023/12/privacy-policy-for-bunkbuddy.html"
        const val SHARED_PREF = "BunkBuddySharedPref"
        const val DARK_THEME = "dark_theme"
        const val NOTIFICATION_ENABLED = "notification_enabled"
    }
}