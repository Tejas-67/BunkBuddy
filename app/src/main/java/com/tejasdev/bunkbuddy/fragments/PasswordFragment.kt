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
import com.tejasdev.bunkbuddy.databinding.FragmentPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordFragment : Fragment() {
    private var _binding: FragmentPasswordBinding? = null
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
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.verifyBtn.setOnClickListener {
            val newPass = binding.newPasswordTextEdit.text.toString()
            val newPassConfirm = binding.confirmPasswordTextEdit.text.toString()
            if(newPass==newPassConfirm){
                changePassword(newPass)
            }
            else showSnackbar(it, "Passwords don't match")
        }
    }

    private fun changePassword(newPass: String) {
        viewModel.changeUserPassword(newPass){ user, message ->
            if(user==null){
                showSnackbar(requireView(), "Something went wrong")
            }
            else{
                showSnackbar(requireView(), "Password changed successfully")
                moveToSettingsFragment()
            }
        }
    }

    private fun showSnackbar(view: View, message: String){
        Snackbar.make(view, message, 2000).show()
    }
    private fun moveToSettingsFragment(){
        findNavController().navigate(R.id.action_passwordFragment_to_settingsFragment)
    }
}