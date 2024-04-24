package com.tejasdev.bunkbuddy.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.activities.AuthActivity
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userName.text = viewModel.getUserName()
        binding.userEmail.text = viewModel.getEmail()
        binding.userImage.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.default_profile
            )
        )
        Glide
            .with(requireContext())
            .load(viewModel.getUserImage())
            .error(R.drawable.default_profile)
            .into(binding.userImage)
        binding.editProfileLl.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.logoutLl.setOnClickListener {
            signOut()
        }

        binding.privacyLl.setOnClickListener {
            showPrivacyPolicy()
        }

        binding.aboutLl.setOnClickListener {
            //
        }

        binding.settingsLl.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
    }

    private fun showPrivacyPolicy() {
        val uri = Uri.parse(MainActivity.PRIVACY_POLICY_LINK)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun signOut() {
        viewModel.signOut()
        val intent = Intent(activity, AuthActivity::class.java)
        startActivity(intent)
    }

}