package com.example.ibucket

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.ibucket.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth

class TestDatabaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_database)
        
        val statusText = findViewById<TextView>(R.id.statusText)
        val testButton = findViewById<Button>(R.id.testButton)
        val repo = FirebaseRepository()
        
        testButton.setOnClickListener {
            statusText.text = "Testing database connection..."
            
            // Test user creation
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                statusText.text = "Please login first to test database"
                return@setOnClickListener
            }
            
            // Test writing to database
            repo.saveUserProfile(
                com.example.ibucket.Model.UserModel(
                    uid = auth.currentUser!!.uid,
                    firstName = "Test",
                    lastName = "User",
                    email = "test@example.com",
                    phone = "1234567890",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            ) { success, error ->
                if (success) {
                    statusText.text = "Database write: SUCCESS"
                    // Test reading from database
                    repo.getUser { user, readError ->
                        if (user != null) {
                            statusText.text = "Database read/write: SUCCESS\nUser: ${user.firstName} ${user.lastName}"
                        } else {
                            statusText.text = "Database read: FAILED\nError: ${readError?.message}"
                        }
                    }
                } else {
                    statusText.text = "Database write: FAILED\nError: ${error?.message}"
                }
            }
        }
    }
}
