package com.tejasdev.bunkbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.databinding.FragmentChangePasswordOtpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordOtpFragment : Fragment() {
    private var _binding: FragmentChangePasswordOtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private val verifyButtonState = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    private fun nextScreen(){
        findNavController().navigate(R.id.action_changePasswordOtpFragment_to_passwordFragment)
    }
    private fun showSnackbar(message: String){
        Snackbar.make(requireView(), message, 2000).show()
    }
}