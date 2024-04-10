package com.example.candlesrush.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.candlesrush.R
import com.example.candlesrush.databinding.ActivityMainBinding
import com.example.candlesrush.models.RegisterModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var navController : NavController
    lateinit var mainmenu: Unit
    private lateinit var mAuth: FirebaseAuth
    var db = Firebase.firestore
    var registerModel = RegisterModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.fragment)
        mAuth = FirebaseAuth.getInstance()
        setSupportActionBar(binding.toolBar)

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

        db.collection("users").document(mAuth.currentUser?.uid.toString()).get().addOnCompleteListener {
            if(it.isSuccessful){
                registerModel = it.result.toObject(RegisterModel::class.java) as RegisterModel
                println("RegisterModel: $registerModel")
            }
        }



        // Save user authentication state using shared preferences


        // Clear saved user authentication state


    }

    private fun logout() {
        mAuth.signOut()
        // Redirect to LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // Close MainActivity
        // Clear any saved user authentication state
        clearSavedUserAuthenticationState()


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        mainmenu=menuInflater.inflate(R.menu.menu_main, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.myOrder ->{
                navController.navigate(R.id.myOrdersListFragment)
                return true
            }
            R.id.logout->{
                logout()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearSavedUserAuthenticationState() {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("userLoggedIn")
        editor.apply()
    }
    private fun saveUserAuthenticationState(userId:String) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("userLoggedIn", true)
        editor.putString("userId", userId)
        editor.apply()
    }
}