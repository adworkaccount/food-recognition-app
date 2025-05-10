package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GetStartedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        val getStartedButton: Button = findViewById(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            // Navigate to the Login screen
            val intent = Intent(this, LoginActivity::class.java) // Replace LoginActivity with your actual login activity class
            startActivity(intent)
            finish() // Optional: Close the GetStartedActivity
        }
    }
}