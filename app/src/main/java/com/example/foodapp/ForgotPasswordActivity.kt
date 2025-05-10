package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var backButtonForgotPassword: ImageButton
    private lateinit var emailEditTextForgotPassword: EditText
    private lateinit var sendVerificationButton: Button
    private lateinit var signupButtonForgotPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        backButtonForgotPassword = findViewById(R.id.back_button_forgot_password)
        emailEditTextForgotPassword = findViewById(R.id.email_edittext_forgot_password)
        sendVerificationButton = findViewById(R.id.send_verification_button)
        signupButtonForgotPassword = findViewById(R.id.signup_button_forgot_password)

        backButtonForgotPassword.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Go back to the previous screen
        }

        sendVerificationButton.setOnClickListener {
            val email = emailEditTextForgotPassword.text.toString().trim()
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditTextForgotPassword.error = "Enter a valid email address"
                return@setOnClickListener
            }
            // In a real application, you would send a password reset request to your backend here.
            // For this example, we'll simulate sending and navigate to the verification screen.
            Toast.makeText(this, "Verification code sent to $email", Toast.LENGTH_SHORT).show()

            // Create an Intent to start the VerificationCodeActivity
            val intent = Intent(this, VerificationCodeActivity::class.java)

            // (Optional) Pass the email to the VerificationCodeActivity if needed
            intent.putExtra("email", email)

            // Start the VerificationCodeActivity
            startActivity(intent)

            // Optionally, you might want to finish the ForgotPasswordActivity here
            // finish()
        }

        signupButtonForgotPassword.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}