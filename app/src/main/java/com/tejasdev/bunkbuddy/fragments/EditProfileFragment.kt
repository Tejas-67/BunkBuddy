package com.tejasdev.bunkbuddy.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    private val PICK_IMAGE_REQUEST = 1
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var userImageUri: Uri = Uri.parse("")

    private var progressVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usernameTextEdit.setText(viewModel.getUserName())
        if(viewModel.getUserImage()!=Uri.parse("")){
            Glide
                .with(requireContext())
                .load(viewModel.getUserImage())
                .into(binding.userIv)
        }

        binding.editImageIv.setOnClickListener {
            openGallery()
        }

        binding.enterBtn.setOnClickListener {
            if(viewModel.hasInternetConnection()){
                if(!progressVisible) {
                    val name = binding.usernameTextEdit.text.toString()
                    if(checkCredentials(name)){
                        showProgressBar()
                        progressVisible = true
                        if(userImageUri == Uri.parse("")){
                            updateUser(name, "")
                        }
                        else{
                            uploadImage {
                                updateUser(name, it.toString())
                            }
                        }
                    }
                }
            }
            else{
                showSnackbar("Internet unavailable")
            }
        }
    }

    private fun updateUser(
        name: String,
        image: String
    ){
        viewModel.updateUserDetails(
            name = name,
            image = image
        ){ user, message ->
            if(user!=null){
                hideProgressBar()
                showSnackbar("Updated Successfully")
                progressVisible = false
                findNavController().popBackStack()
            }
            else{
                progressVisible = false
                hideProgressBar()
                showSnackbar(message?:"Something went wrong")
            }
        }
    }

    private fun uploadImage(callback: (Uri)->Unit){
        val imageRef = storageRef.child("/images/${userImageUri.lastPathSegment}")
        imageRef.putFile(userImageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener {
                        callback(it)
                    }
                    .addOnFailureListener {
                        hideProgressBar()
                        showSnackbar("Something went wrong")
                    }

            }
            .addOnFailureListener {
                hideProgressBar()
                Log.w("image-upload", "$it")
                showSnackbar("Couldn't upload image")
            }
    }

    private fun checkCredentials(name: String): Boolean{
        return name.isNotEmpty() && name.isNotBlank()
    }

    private fun showSnackbar(message: String){
        Snackbar.make(binding.enterBtn, message, 2000).show()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            userImageUri = data.data!!
            binding.userIv.setImageURI(userImageUri)
        }
    }

    fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
        binding.btnText.visibility = View.GONE
    }
    fun hideProgressBar(){
        binding.progressBar.visibility = View.GONE
        binding.btnText.visibility = View.VISIBLE
    }
}