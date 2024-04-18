package com.tejasdev.bunkbuddy.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.activities.AuthActivity
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.activities.OnboardingActivity
import com.tejasdev.bunkbuddy.databinding.FragmentOtpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private val verifyButtonState = MutableLiveData(false)
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireContext().getSharedPreferences(AuthActivity.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        verifyButtonState.observe(viewLifecycleOwner, Observer {
            changeVerifyButtonState(it)
        })
        sendOtp()
        binding.verifyBtn.setOnClickListener {
            verifyOtp(
                binding.otpTextEdit.text.toString()
            )
        }
    }

    private fun changeVerifyButtonState(available: Boolean) {
        val state = if(available) View.VISIBLE else View.GONE
        binding.resendOtp.visibility = state
        binding.verifyBtn.visibility = state
    }

    private fun sendOtp(){
        verifyButtonState.postValue(false)
        viewModel.sendOtp(
            viewModel.getEmail()
        ){ success, message ->
            verifyButtonState.postValue(success)
            showSnackbar(message)
        }
    }

    private fun verifyOtp(userOtp: String){
        verifyButtonState.postValue(false)
        viewModel.verifyOtp(
            viewModel.getEmail(),
            userOtp
        ){success, message ->
            showSnackbar(message)
            if(success){
                nextScreen()
            }
            else{
                verifyButtonState.postValue(true)
            }
        }
    }

    private fun nextScreen() {
        val isFirstTime = sharedPref.getBoolean("isFirstTime", true)
        if(isFirstTime){
            val editor = sharedPref.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()
            moveToOnboardingActivity()
        }
        else moveToMainActivity()
    }

    private fun moveToMainActivity(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun moveToOnboardingActivity(){
        val intent = Intent(requireActivity(), OnboardingActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun moveToMain() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showSnackbar(message: String){
        Snackbar.make(requireView(), message, 2000).show()
    }
}