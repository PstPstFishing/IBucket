package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ibucket.Presenter.RegisterPagePresenter

class RegisterPageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        val FirstName = findViewById<EditText>(R.id.FirstName)
        val MiddleName = findViewById<EditText>(R.id.MiddleName)
        val LastName = findViewById<EditText>(R.id.LastName)
        val Password = findViewById<EditText>(R.id.Password)
        val ConfirmPassword = findViewById<EditText>(R.id.ConfirmPassword)
        val PhoneNumber = findViewById<EditText>(R.id.PhoneNumber)
        val Email = findViewById<EditText>(R.id.Email)

        val TestEmail = intent.getStringExtra("Email").toString()
        val passwordintent = intent.getStringExtra("Password").toString()

        val Presenter = RegisterPagePresenter()
        val SubmitButton = findViewById<Button>(R.id.SubmitButton)
        val BackButton = findViewById<Button>(R.id.BackButton)


        SubmitButton.setOnClickListener(){
            if(Presenter.CheckMinLengthPassword(Password.text.toString())){
                Toast.makeText(this,"Password must be above 6 characters", Toast.LENGTH_SHORT).show()
            }
            else if(Presenter.ValidateEmail(Email.text.toString(),TestEmail)){
                Toast.makeText(this,"Email already registered", Toast.LENGTH_SHORT).show()
            }
            else if(Presenter.ConfirmPassword(Password.text.toString(),ConfirmPassword.text.toString())){
                Toast.makeText(this,"Password does not match", Toast.LENGTH_SHORT).show()
            }
            else if(Presenter.ValidatePhonenumberMax(PhoneNumber.text.toString())){
                Toast.makeText(this,"Phone number must be below 11 characters", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Regstered successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,HomePageActivity:: class.java)
                intent.putExtra("Email",Email.text.toString())
                intent.putExtra("Password",Password.text.toString())
                startActivity(intent)
            }

        }
        BackButton.setOnClickListener(){
            val intent = Intent(this, LoginPageActivity:: class.java)

            startActivity(intent)
        }
    }
}