package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class VerificationCodeActivity : AppCompatActivity() {

    private lateinit var backButtonVerification: ImageButton
    private lateinit var codeEditText1: EditText
    private lateinit var codeEditText2: EditText
    private lateinit var codeEditText3: EditText
    private lateinit var codeEditText4: EditText
    private lateinit var resendCodeTextView: TextView
    private lateinit var verifyButton: Button
    private lateinit var signupButtonVerification: Button
    private var currentFocus: EditText? = null
    private var isResendTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_verification_code)

            backButtonVerification = findViewById(R.id.back_button_verification)
            codeEditText1 = findViewById(R.id.codeEditText1)
            codeEditText2 = findViewById(R.id.codeEditText2)
            codeEditText3 = findViewById(R.id.codeEditText3)
            codeEditText4 = findViewById(R.id.codeEditText4)
            resendCodeTextView = findViewById(R.id.resendCodeTextView)
            verifyButton = findViewById(R.id.verifyButton)
            signupButtonVerification = findViewById(R.id.signupButtonVerification)

            val email = intent.getStringExtra("email")
            Log.d("VerificationCode", "Received email: $email")

            backButtonVerification.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            // Make only "Resend" clickable and handle highlight with OnTouchListener
            val resendText = "If you didn't receive a code, Resend"
            val spannableString = SpannableString(resendText)
            val resendStartIndex = resendText.indexOf("Resend")
            val resendEndIndex = resendStartIndex + "Resend".length
            val normalTextColor = ContextCompat.getColor(this@VerificationCodeActivity, android.R.color.darker_gray) // Using default gray
            val highlightTextColor = ContextCompat.getColor(this@VerificationCodeActivity, R.color.blue)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Toast.makeText(this@VerificationCodeActivity, "Resend code clicked", Toast.LENGTH_SHORT).show()
                    // Implement your actual resend logic here
                    // Revert color after click
                    if (view is TextView) {
                        view.post {
                            setResendTextColor(view, spannableString, highlightTextColor, resendStartIndex, resendEndIndex)
                            isResendTouched = false
                        }
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = highlightTextColor
                    ds.isUnderlineText = false
                }
            }
            spannableString.setSpan(clickableSpan, resendStartIndex, resendEndIndex, 0)
            resendCodeTextView.text = spannableString
            resendCodeTextView.movementMethod = LinkMovementMethod.getInstance()

            resendCodeTextView.setOnTouchListener { v, event ->
                val textView = v as TextView
                val layout = textView.layout
                val x = event.x.toInt()
                val y = event.y.toInt()
                var offset = -1
                if (layout != null) {
                    offset = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x.toFloat())
                }

                val isTouchOnResend = offset in resendStartIndex until resendEndIndex

                if (isTouchOnResend) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        isResendTouched = true
                        setResendTextColor(textView, spannableString, normalTextColor, resendStartIndex, resendEndIndex)
                        return@setOnTouchListener true
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        // Let the ClickableSpan's onClick handle the action and color reset
                        return@setOnTouchListener false
                    } else if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_OUTSIDE) {
                        setResendTextColor(textView, spannableString, highlightTextColor, resendStartIndex, resendEndIndex)
                        isResendTouched = false
                        return@setOnTouchListener false
                    }
                } else {
                    if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                        setResendTextColor(textView, spannableString, highlightTextColor, resendStartIndex, resendEndIndex)
                        isResendTouched = false
                    }
                }
                return@setOnTouchListener textView.onTouchEvent(event)
            }

            // Set initial color
            setResendTextColor(resendCodeTextView, spannableString, highlightTextColor, resendStartIndex, resendEndIndex)

            verifyButton.setOnClickListener {
                val code = "${codeEditText1.text}${codeEditText2.text}${codeEditText3.text}${codeEditText4.text}"
                Log.d("VerificationCode", "Entered code: $code")
                if (code.length == 4) {
                    Toast.makeText(this, "Verifying code: $code", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, NewPasswordActivity::class.java)
                    startActivity(intent)
                    // finish()
                } else {
                    Toast.makeText(this, "Please enter the 4-digit verification code", Toast.LENGTH_SHORT).show()
                }
            }

            signupButtonVerification.setOnClickListener {
                val intent = Intent(this, CreateAccountActivity::class.java)
                startActivity(intent)
            }

            // Use TextWatcher for automatic focus movement
            setupCodeEditTextListeners()

        } catch (e: Exception) {
            Log.e("VerificationCode", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error loading verification screen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setResendTextColor(textView: TextView, spannable: SpannableString, color: Int, start: Int, end: Int) {
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {}
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = color
                ds.isUnderlineText = false
            }
        }, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable
    }

    private fun setupCodeEditTextListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    when (currentFocus) {
                        codeEditText1 -> codeEditText2.requestFocus()
                        codeEditText2 -> codeEditText3.requestFocus()
                        codeEditText3 -> codeEditText4.requestFocus()
                        codeEditText4 -> verifyButton.requestFocus() // Optionally move focus to Verify button
                    }
                } else if (s?.length == 0) {
                    when (currentFocus) {
                        codeEditText4 -> codeEditText3.requestFocus()
                        codeEditText3 -> codeEditText2.requestFocus()
                        codeEditText2 -> codeEditText1.requestFocus()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Optional: Add any logic to be executed after text change
            }
        }

        codeEditText1.addTextChangedListener(textWatcher)
        codeEditText2.addTextChangedListener(textWatcher)
        codeEditText3.addTextChangedListener(textWatcher)
        codeEditText4.addTextChangedListener(textWatcher)

        // Add an OnFocusChangeListener to handle backspace navigation more reliably
        val focusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus) {
                    currentFocus = v as EditText
                }
            }
        }

        codeEditText1.onFocusChangeListener = focusChangeListener
        codeEditText2.onFocusChangeListener = focusChangeListener
        codeEditText3.onFocusChangeListener = focusChangeListener
        codeEditText4.onFocusChangeListener = focusChangeListener

        // Request focus on the first EditText initially
        codeEditText1.requestFocus()
    }
}