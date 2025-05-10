package com.example.foodapp

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FoodResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result)

        val resultFoodImageView: ImageView = findViewById(R.id.resultFoodImageView)
        val resultFoodNameTextView: TextView = findViewById(R.id.resultFoodNameTextView)

        // Get the food name from the Intent extras
        val foodName = intent.getStringExtra("foodName")

        // Get the image URI from the Intent extras
        val imageUri = intent.getParcelableExtra<Uri>("foodImageUri")

        // Display the food name
        foodName?.let {
            resultFoodNameTextView.text = it
        } ?: run {
            resultFoodNameTextView.text = "Could not retrieve food name"
        }

        // Display the food image
        imageUri?.let {
            resultFoodImageView.setImageURI(it)
        }
    }
}