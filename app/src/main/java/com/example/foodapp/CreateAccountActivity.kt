package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var createAccountButton: Button
    private lateinit var googleSignupButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var passwordVisibilityToggle: ImageView
    private var passwordHint: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        nameEditText = findViewById(R.id.name_edittext)
        emailEditText = findViewById(R.id.email_edittext)
        phoneEditText = findViewById(R.id.phone_edittext)
        passwordEditText = findViewById(R.id.password_edittext)
        createAccountButton = findViewById(R.id.create_account_button)
        googleSignupButton = findViewById(R.id.google_signup_button)
        loginTextView = findViewById(R.id.login_textview)
        passwordVisibilityToggle = findViewById(R.id.password_visibility_toggle)

        passwordHint = passwordEditText.hint.toString()

        var passwordVisible = false

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

        createAccountButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (name.isEmpty()) {
                nameEditText.error = "Name is required"
                return@setOnClickListener
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Enter a valid email address"
                return@setOnClickListener
            }

            if (phone.isEmpty() || phone.length < 10) { // Adjust length check as needed
                phoneEditText.error = "Enter a valid phone number"
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) { // Adjust minimum password length as needed
                passwordEditText.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            // For now, just show a success message and navigate home
            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java) // Replace MainActivity if needed
            startActivity(intent)
            finish() // Optional: Close the CreateAccountActivity
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // Replace LoginActivity if needed
            startActivity(intent)
        }

        googleSignupButton.setOnClickListener {
            Toast.makeText(this, "Google Signup Clicked", Toast.LENGTH_SHORT).show()
            // Implement Google Signup logic here later
        }
    }
}