package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ibucket.Presenter.LoginPagePresenter

class LoginPageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val Email = findViewById<EditText>(R.id.Email)
        val Password = findViewById<EditText>(R.id.Password)
        val SubmitButton = findViewById<Button>(R.id.SubmitButton)
        val RegisterButton = findViewById<Button>(R.id.RegisterButton)

        val Presenter =  LoginPagePresenter()
        val TestEmail = "123@Example.com"
        val TestPassword = "1234567"


        SubmitButton.setOnClickListener(){
            if(Presenter.ValidatePassword(Password.text.toString(),TestPassword)){
                 Toast.makeText(this,"Incorrect Password or Email",Toast.LENGTH_SHORT).show()
            }
            else if(Presenter.ValidateEmail(Email.text.toString(),TestEmail)){
                Toast.makeText(this,"Incorrect Password or Email ",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Login Success ",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomePageActivity:: class.java)
                intent.putExtra("Email" ,Email.text.toString())
                intent.putExtra("Password",Password.text.toString())
                startActivity(intent)
            }

        }
        RegisterButton.setOnClickListener(){
            val intent = Intent(this, RegisterPageActivity:: class.java)
            intent.putExtra("Email",TestEmail)
            intent.putExtra("Password",TestPassword)
            startActivity(intent)
        }
    }
}