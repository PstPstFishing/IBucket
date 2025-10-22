package com.example.ibucket

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsPageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)
        findViewById<Button>(R.id.AboutUsButton).setOnClickListener {
            startActivity(android.content.Intent(this, AboutUsActivity::class.java))
        }
        findViewById<Button>(R.id.DeveloperProfileButton).setOnClickListener {
            startActivity(android.content.Intent(this, DevelopersProfileActivity::class.java))
        }
    }
}