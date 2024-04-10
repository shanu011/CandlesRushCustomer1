package com.example.candlesrush.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.candlesrush.R
import com.example.candlesrush.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    val db = Firebase.firestore
    var mAuth = Firebase.auth
    var progressBar: ProgressBar?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleSmall)
        progressBar=binding.pbar

        binding.tvregister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }
//
        binding.btnsave.setOnClickListener {
            binding.pbar.visibility=View.VISIBLE

            mAuth.signInWithEmailAndPassword(
                binding.edtemail.text.toString(),
                binding.edtPassword.text.toString()
            ).addOnCompleteListener { loginTask ->
                if (loginTask.isSuccessful) {
                    // Login successful
                    binding.pbar.visibility= View.GONE

                    Snackbar.make(
                        binding.edtemail,
                        "Login Successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    this.finish()
                    println("Login Successfully")
                } else {
                    // Login failed
                    Snackbar.make(
                        binding.edtemail,
                        "Login Error",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    println("Login Error: ${loginTask.exception}")
                }
            }
        }



    }


    fun login(){

    }
}