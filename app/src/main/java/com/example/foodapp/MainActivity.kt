package com.example.foodapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var addIcon: ImageView
    private lateinit var popOutMenuContainer: FrameLayout
    private lateinit var closeButton: ImageView
    private lateinit var cameraOptionLayout: LinearLayout
    private lateinit var galleryOptionLayout: LinearLayout
    private lateinit var overlayView: View
    private lateinit var greetingTextView: TextView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageView
    private lateinit var homeIcon: ImageView
    private lateinit var historyIcon: ImageView
    private lateinit var profileIcon: ImageView

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var currentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        addIcon = findViewById(R.id.addIcon)
        popOutMenuContainer = findViewById(R.id.popOutMenuContainer)
        closeButton = findViewById(R.id.closeButton)
        cameraOptionLayout = findViewById(R.id.cameraOption)
        galleryOptionLayout = findViewById(R.id.galleryOption)
        overlayView = findViewById(R.id.overlayView)
        greetingTextView = findViewById(R.id.greetingTextView)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        homeIcon = findViewById(R.id.homeIcon)
        historyIcon = findViewById(R.id.historyIcon)
        profileIcon = findViewById(R.id.profileIcon)

        // Set click listener for the add icon to show the pop-up
        addIcon.setOnClickListener {
            popOutMenuContainer.visibility = View.VISIBLE
            overlayView.visibility = View.VISIBLE
        }

        // Set click listener for the close button to hide the pop-up
        closeButton.setOnClickListener {
            popOutMenuContainer.visibility = View.GONE
            overlayView.visibility = View.GONE
        }

        // Initialize ActivityResultLauncher for gallery
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    Log.d("Gallery", "Selected Image URI: $it")
                    Toast.makeText(this, "Gallery Image Selected", Toast.LENGTH_SHORT).show()
                    // Start ImageDisplayActivity and pass the URI
                    val intent = Intent(this, ImageDisplayActivity::class.java)
                    intent.putExtra("imageUri", it)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
            popOutMenuContainer.visibility = View.GONE
            overlayView.visibility = View.GONE
        }

        // Initialize ActivityResultLauncher for camera
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoUri?.let { uri ->
                    Log.d("Camera", "Captured Image URI: $uri")
                    Toast.makeText(this, "Camera Image Captured", Toast.LENGTH_SHORT).show()
                    // Start ImageDisplayActivity and pass the URI
                    val intent = Intent(this, ImageDisplayActivity::class.java)
                    intent.putExtra("imageUri", uri)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Camera capture failed", Toast.LENGTH_SHORT).show()
            }
            popOutMenuContainer.visibility = View.GONE
            overlayView.visibility = View.GONE
        }

        // Set click listener for the gallery option in the pop-up
        galleryOptionLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            galleryLauncher.launch(intent)
        }

        // Set click listener for the camera option in the pop-up
        cameraOptionLayout.setOnClickListener {
            createImageFile()?.let { uri ->
                currentPhotoUri = uri
                cameraLauncher.launch(uri)
            }
        }

        // Set click listeners for bottom navigation icons (placeholders)
        homeIcon.setOnClickListener {
            Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to home screen
        }

        historyIcon.setOnClickListener {
            Toast.makeText(this, "History Clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to history screen
        }

        profileIcon.setOnClickListener {
            Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to profile screen
        }

        // Set click listener for the search button
        searchButton.setOnClickListener {
            val searchText = searchEditText.text.toString()
            Toast.makeText(this, "Search: $searchText", Toast.LENGTH_SHORT).show()
            // TODO: Implement search functionality
        }
    }

    // Function to create a temporary file for camera images
    private fun createImageFile(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return try {
            val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
            FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)
        } catch (e: IOException) {
            Log.e("Camera", "Could not create image file", e)
            null
        }
    }
}