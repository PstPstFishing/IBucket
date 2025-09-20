package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val EmailTextView = findViewById<TextView>(R.id.TextViewEmail)
        val LogoutButton = findViewById<Button>(R.id.LogoutButton)

        val Email = intent.getStringExtra("Email")
        val password = intent.getStringExtra("Password")

        EmailTextView.text = "Hello " + Email
        LogoutButton.setOnClickListener(){
            val intent = Intent(this,LoginPageActivity:: class.java)
            intent.putExtra("Email",Email)
            intent.putExtra("Password",password)
            startActivity(intent)
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Handle Home navigation
                    true
                }
                R.id.nav_profile -> {
                    // Handle Profile navigation
                    true
                }
                R.id.nav_settings -> {
                    // Handle Settings navigation
                    true
                }
                else -> false
            }
        }
    }
}