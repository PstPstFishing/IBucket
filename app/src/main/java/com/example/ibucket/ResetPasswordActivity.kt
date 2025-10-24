package com.example.ibucket

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : Activity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val email = intent.getStringExtra("email") ?: ""
        auth = FirebaseAuth.getInstance()

        val currentPassword = findViewById<EditText>(R.id.CurrentPassword)
        val newPassword = findViewById<EditText>(R.id.NewPassword)
        val confirmPassword = findViewById<EditText>(R.id.ConfirmPassword)
        val submit = findViewById<Button>(R.id.SubmitButton)

        submit.setOnClickListener {
            val curr = currentPassword.text.toString()
            val newP = newPassword.text.toString()
            val conf = confirmPassword.text.toString()
            if(curr.isEmpty() || newP.isEmpty() || conf.isEmpty()){
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            if(newP.length < 8){
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            if(newP != conf){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            val user = auth.currentUser
            if(user == null){
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            val credential = EmailAuthProvider.getCredential(email, curr)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if(reauthTask.isSuccessful){
                    user.updatePassword(newP).addOnCompleteListener { updTask ->
                        if(updTask.isSuccessful){
                            Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, updTask.exception?.localizedMessage ?: "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, reauthTask.exception?.localizedMessage ?: "Re-auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


