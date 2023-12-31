package com.tejasdev.bunkbuddy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.databinding.ActivityAuthBinding
import com.tejasdev.bunkbuddy.databinding.ActivityMainBinding
import com.tejasdev.bunkbuddy.repository.AuthRepository

class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    lateinit var viewModel: AuthViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = AuthViewmodel(application, this)
        if(viewModel.isLogin() || viewModel.isSkipped()){
            moveToMainActivity()
        }
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        supportActionBar?.hide()
        setupActionBarWithNavController(navController)
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object{
        const val SHARED_PREFERENCE = "bunkbuddy_shared_preference"
    }
}