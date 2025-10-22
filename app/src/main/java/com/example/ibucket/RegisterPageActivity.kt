package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ibucket.Presenter.RegisterPagePresenter
import com.example.ibucket.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterPageActivity : Activity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        val firstName = findViewById<EditText>(R.id.FirstName)
        val middleName = findViewById<EditText>(R.id.MiddleName)
        val lastName = findViewById<EditText>(R.id.LastName)
        val password = findViewById<EditText>(R.id.Password)
        val confirmPassword = findViewById<EditText>(R.id.ConfirmPassword)
        val phoneNumber = findViewById<EditText>(R.id.PhoneNumber)
        val emailField = findViewById<EditText>(R.id.Email)

        val presenter = RegisterPagePresenter()
        val submitButton = findViewById<Button>(R.id.SubmitButton)
        val backButton = findViewById<Button>(R.id.BackButton)

        auth = FirebaseAuth.getInstance()

        submitButton.setOnClickListener {
            val first = firstName.text.toString().trim()
            val middle = middleName.text.toString().trim()
            val last = lastName.text.toString().trim()
            val pass = password.text.toString()
            val confirm = confirmPassword.text.toString()
            val phone = phoneNumber.text.toString().trim()
            val email = emailField.text.toString().trim()

            if (presenter.ValidateEmpty(first) || presenter.ValidateEmpty(last) ||
                presenter.ValidateEmpty(email) || presenter.ValidateEmpty(pass) ||
                presenter.ValidateEmpty(confirm)
            ) {
                Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (presenter.CheckMinLengthPassword(pass)) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (presenter.ConfirmPassword(pass, confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (presenter.ValidatePhonenumberMax(phone)) {
                Toast.makeText(this, "Phone must be 10–11 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                    val now = System.currentTimeMillis()
                    val user = UserModel(uid, first, middle, last, email, phone, now, now)

                    val db = FirebaseDatabase.getInstance("https://ibucket-e19f3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    db.getReference("users").child(uid).setValue(user)
                        .addOnCompleteListener { saveTask ->
                            if (saveTask.isSuccessful) {
                                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginPageActivity::class.java)
                                intent.putExtra("Email", email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, saveTask.exception?.localizedMessage ?: "Failed to save profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, LoginPageActivity::class.java))
            finish()
        }
    }

}
