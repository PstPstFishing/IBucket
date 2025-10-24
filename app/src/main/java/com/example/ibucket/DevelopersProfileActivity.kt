package com.example.ibucket

import android.app.Activity
import android.os.Bundle

class DevelopersProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developers_profile)
        val back = android.widget.Button(this)
        back.text = "Back"
        back.setOnClickListener { finish() }
    }
}


