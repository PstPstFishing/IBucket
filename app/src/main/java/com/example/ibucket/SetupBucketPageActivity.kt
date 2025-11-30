package com.example.ibucket

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ibucket.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SetupBucketPageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_bucket_page)

        val nameField = findViewById<EditText>(R.id.BucketName)
        val heightField = findViewById<EditText>(R.id.BucketHeight)
        val thresholdField = findViewById<EditText>(R.id.ThresholdPercent)
        val saveButton = findViewById<Button>(R.id.SaveConfigButton)
        val repo = FirebaseRepository()

        saveButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val heightText = heightField.text.toString().trim()
            val thresholdText = thresholdField.text.toString().trim()

            if (name.isEmpty() || heightText.isEmpty() || thresholdText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightMm = (heightText.toDoubleOrNull()?.times(10))?.toInt() ?: run {
                Toast.makeText(this, "Invalid height", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }

            val threshold = thresholdText.toIntOrNull()?.coerceIn(0, 100) ?: run {
                Toast.makeText(this, "Invalid threshold", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }

            repo.createBucket(name, heightMm, threshold) { bucketId, err ->
                if (bucketId != null) {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    val ref = FirebaseDatabase.getInstance().reference

                    val updates = mapOf(
                        "/userBuckets/$uid/$bucketId" to true,
                        "/devices/ESP32_001/bucketId" to bucketId // Assign device!
                    )

                    ref.updateChildren(updates).addOnCompleteListener {
                        Toast.makeText(this, "Bucket linked to device!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, err?.localizedMessage ?: "Failed to create bucket", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
