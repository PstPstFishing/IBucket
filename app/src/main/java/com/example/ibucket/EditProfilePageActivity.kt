package com.example.ibucket

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ibucket.Model.UserModel
import com.example.ibucket.data.FirebaseRepository

class EditProfilePageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_page)

        val firstField = findViewById<EditText>(R.id.editFirstName)
        val middleField = findViewById<EditText>(R.id.editMiddleName)
        val lastField = findViewById<EditText>(R.id.editLastName)
        val emailField = findViewById<EditText>(R.id.editEmail)
        val phoneField = findViewById<EditText>(R.id.editPhone)
        val saveBtn = findViewById<Button>(R.id.btnSave)
        val cancelBtn = findViewById<Button>(R.id.btnCancel)
        val currentPass = findViewById<EditText>(R.id.currentPassword)
        val newPass = findViewById<EditText>(R.id.newPassword)
        val confirmNew = findViewById<EditText>(R.id.confirmNewPassword)
        val changePassBtn = findViewById<Button>(R.id.btnChangePassword)
        val repo = FirebaseRepository()

        repo.getUser { user, _ ->
            user?.let {
                firstField.setText(it.firstName)
                middleField.setText(it.middleName)
                lastField.setText(it.lastName)
                emailField.setText(it.email)
                phoneField.setText(it.phone)
            }
        }

        saveBtn.setOnClickListener {
            val first = firstField.text.toString().trim()
            val middle = middleField.text.toString().trim()
            val last = lastField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val phone = phoneField.text.toString().trim()
            if(first.isEmpty() || last.isEmpty() || email.isEmpty()){
                Toast.makeText(this, "First, Last and Email required", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            if(phone.length > 11){
                Toast.makeText(this, "Phone must be 10-11 digits", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val now = System.currentTimeMillis()
            val user = UserModel(uid, first, middle, last, email, phone, now, now)
            repo.saveUserProfile(user){ ok, err ->
                if(ok){
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show(); finish()
                } else {
                    Toast.makeText(this, err?.localizedMessage ?: "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cancelBtn.setOnClickListener { finish() }

        changePassBtn.setOnClickListener {
            val curr = currentPass.text.toString()
            val np = newPass.text.toString()
            val cp = confirmNew.text.toString()
            if(curr.isEmpty() || np.isEmpty() || cp.isEmpty()){
                Toast.makeText(this, "Fill all password fields", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            if(np.length < 8){
                Toast.makeText(this, "New password must be at least 8 characters", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            if(np != cp){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            val email = emailField.text.toString().trim()
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener
            val cred = com.google.firebase.auth.EmailAuthProvider.getCredential(email, curr)
            user.reauthenticate(cred).addOnCompleteListener { rt ->
                if(rt.isSuccessful){
                    user.updatePassword(np).addOnCompleteListener { ut ->
                        if(ut.isSuccessful){
                            Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                            currentPass.text.clear(); newPass.text.clear(); confirmNew.text.clear()
                        } else {
                            Toast.makeText(this, ut.exception?.localizedMessage ?: "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, rt.exception?.localizedMessage ?: "Re-auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}