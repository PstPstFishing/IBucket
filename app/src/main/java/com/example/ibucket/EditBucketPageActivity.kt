package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ibucket.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditBucketPageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_bucket_page)

        val bucketId = intent.getStringExtra("bucketId") ?: return
        val heightEt = findViewById<EditText>(R.id.EditBucketHeightEditText)
        val thresholdEt = findViewById<EditText>(R.id.EditThresholdPercentEditText)
        val saveBtn = findViewById<Button>(R.id.EditSaveConfigButton)
        val deleteBtn = findViewById<Button>(R.id.EditBucketDeleteButton)
        val repo = FirebaseRepository()

        repo.getBucket(bucketId) { bucket, _ ->
            bucket?.let {
                heightEt.setText((it.heightMm / 10.0).toString())
                thresholdEt.setText(it.thresholdPercent.toString())
            }
        }

        saveBtn.setOnClickListener {
            val heightCm = heightEt.text.toString().toDoubleOrNull()
            val threshold = thresholdEt.text.toString().toIntOrNull()

            if (heightCm == null || threshold == null) {
                Toast.makeText(this, "Invalid height or threshold", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightMm = (heightCm * 10).toInt()
            val updates = hashMapOf<String, Any>(
                "heightMm" to heightMm,
                "thresholdPercent" to threshold.coerceIn(0, 100),
                "updatedAt" to System.currentTimeMillis()
            )

            FirebaseDatabase.getInstance().getReference("buckets").child(bucketId)
                .updateChildren(updates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Bucket updated", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.localizedMessage ?: "Update failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        deleteBtn.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val ref = FirebaseDatabase.getInstance().reference
            val updates = hashMapOf<String, Any?>(
                "/buckets/$bucketId" to null,
                "/userBuckets/$uid/$bucketId" to null,
                "/readings/$bucketId" to null,
                "/devices/ESP32_001/bucketId" to null
            )

            ref.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Bucket deleted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomePageActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage ?: "Delete failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
