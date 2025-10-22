package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.ibucket.data.FirebaseRepository

class ProfilePageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        val repo = FirebaseRepository()
        val editBtn = findViewById<Button>(R.id.EditProfileButton)

        val nameTv = findViewById<TextView>(R.id.nameText)
        val firstNameTv = findViewById<TextView>(R.id.firstNameText)
        val middleNameTv = findViewById<TextView>(R.id.middleNameText)
        val lastNameTv = findViewById<TextView>(R.id.lastNameText)
        val emailTv = findViewById<TextView>(R.id.emailText)
        val phoneTv = findViewById<TextView>(R.id.phoneText)

        repo.getUser { user, _ ->
            user?.let {
                nameTv.text = "${it.firstName} ${it.lastName}"
                firstNameTv.text = it.firstName
                middleNameTv.text = it.middleName
                lastNameTv.text = it.lastName
                emailTv.text = it.email
                phoneTv.text = it.phone
            }
        }

        editBtn.setOnClickListener {
            startActivity(Intent(this, EditProfilePageActivity::class.java))
        }
    }
}
