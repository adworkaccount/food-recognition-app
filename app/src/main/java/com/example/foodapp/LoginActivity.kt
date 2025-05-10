package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpTextView: TextView
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var googleLoginButton: Button
    private lateinit var passwordVisibilityToggle: ImageView
    private var passwordHint: String? = null
    private var passwordVisible = false // Keep track of password visibility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.email_edittext)
        passwordEditText = findViewById(R.id.password_edittext)
        signInButton = findViewById(R.id.signin_button)
        signUpTextView = findViewById(R.id.signup_textview)
        forgotPasswordTextView = findViewById(R.id.forgot_password_textview)
        googleLoginButton = findViewById(R.id.google_login_button)
        passwordVisibilityToggle = findViewById(R.id.password_visibility_toggle)

        passwordHint = passwordEditText.hint.toString()

        passwordVisibilityToggle.setOnClickListener {
            val currentText = passwordEditText.text.toString()

            if (currentText.isNotEmpty()) {
                passwordEditText.text.clear()
                passwordEditText.hint = null

                passwordVisible = !passwordVisible
                if (passwordVisible) {
                    passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility)
                } else {
                    passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off)
                }

                passwordEditText.setText(currentText)
                passwordEditText.setSelection(currentText.length)
                passwordEditText.hint = passwordHint
            }
        }

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Implement sign-in logic here (e.g., Firebase Auth)
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Example (replace with your actual logic):
                Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        googleLoginButton.setOnClickListener {
            // Implement Google login logic
            Toast.makeText(this, "Google Login Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}