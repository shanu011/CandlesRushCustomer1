package com.example.candlesrush.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import com.example.candlesrush.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    var mAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler(Looper.getMainLooper()).postDelayed(
            {
                mAuth = FirebaseAuth.getInstance()
                // Check if a user is currently logged in
                val currentUser = mAuth.currentUser
                if (currentUser != null) {
                    // User is logged in, you can access the user details
                    val userId = currentUser.uid
                    val userEmail = currentUser.email
                    // ... other user details

                    saveUserAuthenticationState(userId)
                    // Example: Display a welcome message
                    Toast.makeText(
                        this,
                        "Welcome, $userEmail!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // No user is currently logged in, redirect to the login screen
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish() // Close MainActivity to prevent going back to it after login
                }
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
        }, 3000)
    }
    private fun saveUserAuthenticationState(userId:String) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("userLoggedIn", true)
        editor.putString("userId", userId)
        editor.apply()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}
