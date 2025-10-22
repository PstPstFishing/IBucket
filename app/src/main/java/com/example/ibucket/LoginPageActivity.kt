package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ibucket.Presenter.LoginPagePresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginPageActivity : Activity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val emailField = findViewById<EditText>(R.id.Email)
        val passwordField = findViewById<EditText>(R.id.Password)
        val submitButton = findViewById<Button>(R.id.SubmitButton)
        val registerButton = findViewById<Button>(R.id.RegisterButton)
        val forgotButton = findViewById<Button?>(R.id.ForgotPasswordButton)

        val presenter = LoginPagePresenter()
        auth = FirebaseAuth.getInstance()

        submitButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            if (presenter.ValidateEmpty(email) || presenter.ValidateEmpty(password)) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (presenter.CheckMinLengthPassword(password)) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val db = FirebaseDatabase.getInstance("https://ibucket-e19f3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        val userRef = db.getReference("users").child(uid)

                        userRef.get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomePageActivity::class.java)
                                intent.putExtra("Email", email)
                                startActivity(intent)
                                finish()
                            } else {
                                auth.signOut()
                                Toast.makeText(this, "User not found in database. Contact support.", Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Database check failed: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterPageActivity::class.java)
            startActivity(intent)
        }

        forgotButton?.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

}
