package com.example.candlesrush.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import com.example.candlesrush.R
import com.example.candlesrush.databinding.ActivityRegisterBinding
import com.example.candlesrush.models.RegisterModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    lateinit var binding:ActivityRegisterBinding
    val db = Firebase.firestore
    var progressBar: ProgressBar?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mAuht = Firebase.auth
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleSmall)
        progressBar=binding.pbar

        binding.btnsave.setOnClickListener {
            binding.pbar.visibility=View.VISIBLE
            mAuht.createUserWithEmailAndPassword( binding.edtemail.text.toString(), binding.edtPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        val user = mAuht.currentUser
                        val registerModel = RegisterModel()
                        registerModel.username = binding.edtitems.text.toString()
                        registerModel.useremail = binding.edtemail.text.toString()
                        registerModel.userId = user?.uid
                        // Save user details to Firestore database
                        db.collection("users").document(mAuht.currentUser?.uid.toString()) .set(registerModel)
                            .addOnCompleteListener { registrationTask ->
                                if (registrationTask.isSuccessful) {
                                    binding.pbar.visibility=View.GONE
                                    // Registration and data save successful
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    // Error saving user data
                                    // Handle error appropriately

                                }
                            }
                    } else {
                        // Registration failed
                        // Handle error appropriately
                    }
                }
        }
    }
}