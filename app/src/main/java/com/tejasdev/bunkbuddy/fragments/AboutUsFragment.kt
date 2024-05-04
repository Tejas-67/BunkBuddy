package com.tejasdev.bunkbuddy.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.tejasdev.bunkbuddy.databinding.FragmentAboutUsBinding
import com.tejasdev.bunkbuddy.util.AppReviewHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutUsFragment : Fragment(){
    private var _binding: FragmentAboutUsBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var reviewManager: ReviewManager
    @Inject lateinit var reviewHelper: AppReviewHelper

    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(reviewHelper.hasReviewed()){
            binding.reviewBtnText.text = "Thanks for rating us!"
        }
        binding.sendFeedbackBtn.setOnClickListener {
            sendEmail(
                "tejas.dev.2023@gmail.com",
                "Bunkbuddy is amazing",
                ""
            )
        }
        binding.reviewButton.setOnClickListener {
            if(!reviewHelper.hasReviewed()) startInAppReview()
        }
        binding.github.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tejas-67/bunkbuddy"))
            startActivity(intent)
        }
    }
    fun sendEmail(recipient: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        startActivity(intent)
    }


    private fun startInAppReview(){
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task->
            if(task.isSuccessful){
                val info = task.result
                val flow = reviewManager.launchReviewFlow(activity as Activity, info)
                flow.addOnCompleteListener {
                    reviewHelper.setReviewed()
                }
            }
            else{
                val errorCode = 0
                Log.w("review", errorCode.toString())
                Snackbar.make(requireView(), "Something went wrong: $errorCode", 2000).show()
            }
        }
    }
}