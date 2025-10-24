package com.example.ibucket

import android.app.Activity
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
            if(email.isEmpty()){
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Check if email exists in users node, then proceed to reset screen without email link
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("email").equalTo(email).limitToFirst(1)
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful && task.result.exists()){
                        val intent = android.content.Intent(this, ResetPasswordActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}


