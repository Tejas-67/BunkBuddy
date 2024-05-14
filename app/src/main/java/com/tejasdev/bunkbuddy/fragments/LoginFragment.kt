package com.tejasdev.bunkbuddy.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.activities.AuthActivity
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.activities.OnboardingActivity
import com.tejasdev.bunkbuddy.databinding.FragmentLoginBinding
import com.tejasdev.bunkbuddy.datamodel.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private var enterBtnState: MutableLiveData<Boolean> = MutableLiveData(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(viewModel.isLogin()){
            moveToMainActivity()
        }
        binding.signUpTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        sharedPref = requireActivity().getSharedPreferences(AuthActivity.SHARED_PREFERENCE, Context.MODE_PRIVATE)

        binding.enterBtn.setOnClickListener {
            if(viewModel.hasInternetConnection()){
                if(enterBtnState.value!!){
                    val email = binding.emailTextEdit.text.toString()
                    val password = binding.passwordTextEdit.text.toString()
                    if(checkCredentials(email, password)){
                        showProgressBar()
                        viewModel.loginUser(email, password){ user, message ->
                            if(user==null) {
                                hideProgressBar()
                                showSnackbar(message ?: "Unknown error")
                            }
                            else {
                                if(!user.isVerified) {
                                    val action = LoginFragmentDirections.actionLoginFragmentToOtpFragment(user)
                                    findNavController().navigate(action)
                                }
                                else{
                                    createSesssion(user)
                                    nextActivity()
                                }
                            }
                        }
                    }
                }
            }
            else showSnackbar("Internet not available")
        }

    }
    private fun showProgressBar(){
        enterBtnState.postValue(false)
        binding.progressBar.visibility = View.VISIBLE
        binding.btnText.text = ""
    }
    private fun hideProgressBar(){
        enterBtnState.postValue(true)
        binding.progressBar.visibility = View.GONE
        binding.btnText.text = "Enter"
    }
    private fun nextActivity(){
        val isFirstTime = sharedPref.getBoolean("isFirstTime", true)
        if(isFirstTime){
            val editor = sharedPref.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()
            moveToOnboardingActivity()
        }
        else moveToMainActivity()
    }

    private fun moveToOnboardingActivity() {
        val intent = Intent(requireActivity(), OnboardingActivity::class.java)
        startActivity(intent)
        (activity as AuthActivity).finish()
    }

    private fun createSesssion(user: User){
        viewModel.createSession(user)
    }

    private fun moveToMainActivity(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        (activity as AuthActivity).finish()
    }

    private fun checkCredentials(email: String, password: String): Boolean{
        if(password.isEmpty()){
            showSnackbar("Password cannot be empty")
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showSnackbar("Email not valid")
            return false
        }
        return true
    }
    private fun showSnackbar(message: String){
        Snackbar.make(requireView(), message, 200).show()
    }
}