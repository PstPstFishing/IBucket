package com.example.ibucket

import android.app.Activity
import android.os.Bundle

class AboutUsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        val back = android.widget.Button(this)
        back.text = "Back"
        back.setOnClickListener { finish() }
    }
}


