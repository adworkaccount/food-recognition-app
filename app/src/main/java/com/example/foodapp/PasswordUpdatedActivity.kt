package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PasswordUpdatedActivity : AppCompatActivity() {

    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_updated)

        loginButton = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            // Clear the back stack so the user can't go back to the reset flow
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}