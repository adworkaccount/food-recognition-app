package com.example.foodapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.exp

class ImageDisplayActivity : AppCompatActivity() {

    private lateinit var displayedImageView: ImageView
    private lateinit var analyzeButton: Button
    private lateinit var backButton: AppCompatImageButton
    private lateinit var imageProcessor: ImageProcessor
    private var imageUri: Uri? = null
    private var tfliteInterpreter: Interpreter? = null
    private val imageSize = 224 // Assuming 224x224 input
    private val colorComponents = 3 // RGB
    private var labels: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        displayedImageView = findViewById(R.id.displayedImageView)
        analyzeButton = findViewById(R.id.analyzeButton)
        backButton = findViewById(R.id.backButton)

        // Initialize ImageProcessor for resizing and normalization for EfficientNet
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(imageSize, imageSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.485f, 0.229f)) // Red channel mean and std
            .add(NormalizeOp(0.456f, 0.224f)) // Green channel mean and std
            .add(NormalizeOp(0.406f, 0.225f)) // Blue channel mean and std
            .build()

        // Load model and labels
        try {
            val modelBuffer = FileUtil.loadMappedFile(this, "food_recognition_model.tflite")
            tfliteInterpreter = Interpreter(modelBuffer)
            labels = FileUtil.loadLabels(this, "labels.txt")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ImageDisplayActivity", "Error loading model or labels: ${e.message}")
            // Consider showing an error message to the user
        }

        imageUri = intent.getParcelableExtra("imageUri")
        imageUri?.let {
            displayedImageView.setImageURI(it)
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        analyzeButton.setOnClickListener {
            Log.d("ImageDisplayActivity", "Analyze button clicked!")
            imageUri?.let { uri ->
                val bitmap = getBitmapFromUri(this, uri)
                bitmap?.let {
                    Log.d("ImageDisplayActivity", "Original Bitmap Width: ${it.width}, Height: ${it.height}")
                    recognizeFood(it)
                } ?: run {
                    Log.e("ImageDisplayActivity", "Error loading bitmap")
                    navigateToResult("Error loading image", uri)
                }
            }
        }
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ImageDisplayActivity", "Error getting bitmap from URI: ${e.message}")
            null
        }
    }

    private fun recognizeFood(bitmap: Bitmap) {
        tfliteInterpreter?.let { interpreter ->
            Log.d("ImageDisplayActivity", "recognizeFood() called")
            if (labels.isEmpty()) {
                Log.e("ImageDisplayActivity", "Labels not loaded properly.")
                return
            }

            // Create a TensorImage from the Bitmap
            var tensorImage = TensorImage.fromBitmap(bitmap)

            // Resize and normalize the image using the ImageProcessor
            tensorImage = imageProcessor.process(tensorImage)

            // Get the input TensorBuffer
            val inputTensorBuffer = tensorImage.tensorBuffer

            // Log the first 20 float values of the input tensor
            logInputTensorValues(inputTensorBuffer)

            // Get the output tensor
            val outputTensor = interpreter.getOutputTensor(0)
            Log.d("ImageDisplayActivity", "Output Tensor Shape: " + outputTensor.shape().toList().toString() + ", Type: " + outputTensor.dataType().toString())

            // Allocate output buffer
            val outputShape = outputTensor.shape()
            val outputDataType = outputTensor.dataType()
            val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType)

            // Run the inference
            interpreter.run(inputTensorBuffer.buffer, outputBuffer.buffer)

            // Process the output based on the output data type
            val foodName = when (outputDataType) {
                DataType.FLOAT32 -> processFloatOutput(outputBuffer.floatArray)
                // Add other data type handling if your model outputs something else (e.g., UINT8 with dequantization)
                else -> {
                    Log.e("ImageDisplayActivity", "Unsupported output data type: $outputDataType")
                    "Could not identify food (Unsupported output)"
                }
            }

            Log.d("ImageDisplayActivity", "Inference completed")
            Log.d("ImageDisplayActivity", "Identified food: $foodName")
            navigateToResult(foodName, imageUri)

        } ?: run {
            Log.e("ImageDisplayActivity", "TFLite interpreter is null.")
            navigateToResult("Model not loaded", imageUri)
        }
    }

    private fun processFloatOutput(probabilities: FloatArray): String {
        val maxProbabilityIndex = probabilities.indices.maxByOrNull { index: Int -> probabilities[index] } ?: -1
        return if (maxProbabilityIndex != -1 && maxProbabilityIndex < labels.size) {
            labels[maxProbabilityIndex]
        } else {
            "Could not identify food (Invalid index)"
        }
    }

    // If your model outputs logits, you might need a softmax function like this:
    private fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: return logits
        val exponents = logits.map { exp(it - maxLogit) }
        val sumOfExponentials = exponents.sum()
        return exponents.map { it / sumOfExponentials }.toFloatArray()
    }

    private fun logInputTensorValues(inputTensorBuffer: TensorBuffer) {
        val floatBuffer = inputTensorBuffer.floatArray
        val numValuesToLog = minOf(20, floatBuffer.size) // Log up to 20 values
        val valuesString = (0 until numValuesToLog).joinToString(", ") { String.format("%.4f", floatBuffer[it]) }
        Log.d("ImageDisplayActivity", "First $numValuesToLog Input Tensor Values: $valuesString")
    }

    private fun navigateToResult(foodName: String, imageUri: Uri?) {
        val intent = Intent(this, FoodResultActivity::class.java)
        intent.putExtra("foodName", foodName)
        intent.putExtra("foodImageUri", imageUri)
        Log.d("ImageDisplayActivity", "Starting FoodResultActivity")
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        tfliteInterpreter?.close()
    }
}