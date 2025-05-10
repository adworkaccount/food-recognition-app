package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var backButtonNewPassword: ImageButton
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        backButtonNewPassword = findViewById(R.id.back_button_new_password)
        newPasswordEditText = findViewById(R.id.new_password_edittext)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext)
        submitButton = findViewById(R.id.submit_button)

        backButtonNewPassword.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        submitButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (newPassword.length < 8) {
                newPasswordEditText.error = "Password must be at least 8 digits"
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                confirmPasswordEditText.error = "Passwords do not match"
                return@setOnClickListener
            }

            // In a real application, you would send the new password to your backend here
            Toast.makeText(this, "Password reset successful!", Toast.LENGTH_LONG).show()

            // Navigate to the Password Updated Activity
            val intent = Intent(this, PasswordUpdatedActivity::class.java)
            startActivity(intent)
            finish() // Finish the NewPasswordActivity
        }
    }
}