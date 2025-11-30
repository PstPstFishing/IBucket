package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : Activity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailField = findViewById<EditText>(R.id.Email)
        val submitButton = findViewById<Button>(R.id.SubmitButton)

        auth = FirebaseAuth.getInstance()

        submitButton.setOnClickListener {
            val email = emailField.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_LONG).show()
                    finish() // Go back to login
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Failed to send email", Toast.LENGTH_LONG).show()
                }
            }
            val backButton = findViewById<Button>(R.id.BackButton)
            backButton.setOnClickListener {
                val intent = Intent(this, LoginPageActivity::class.java)
            }
        }
    }
}


