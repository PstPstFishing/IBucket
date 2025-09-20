package com.example.ibucket

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LandingPageActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val continuebutton = findViewById<Button>(R.id.ContinueButton)

        continuebutton.setOnClickListener(){
            val intent = Intent(this, LoginPageActivity:: class.java)
            startActivity(intent)
        }
    }
}