package com.tejasdev.bunkbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.databinding.FragmentChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.continueBtn.setOnClickListener {
            val password = binding.currentPasswordTextEdit.text.toString()
            if(password.isEmpty()) showSnackBar(it, "Password cannot be empty")
            else{
                if(password == viewModel.getPassword()){
                    findNavController().navigate(R.id.action_changePasswordFragment_to_passwordFragment)
                }
                else{
                    showSnackBar(it, "Incorrect password")
                }
            }
        }
        binding.continueWithOtpLl.setOnClickListener {
            findNavController().navigate(R.id.action_changePasswordFragment_to_changePasswordOtpFragment)
        }
    }
    private fun showSnackBar(view: View = requireView(), message: String){
        Snackbar.make(view, message, 2000).show()
    }
}