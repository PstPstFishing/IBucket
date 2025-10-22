package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class BucketDetailsPageActivity : Activity() {

    private lateinit var bucketRef: DatabaseReference
    private lateinit var readingsRef: DatabaseReference
    private lateinit var bucketNameTv: TextView
    private lateinit var levelTv: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private val data = mutableListOf<String>()
    private val firebaseUrl =
        "https://ibucket-e19f3-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bucket_details_page)

        val bucketId = intent.getStringExtra("bucketId")
        if (bucketId.isNullOrEmpty()) {
            Toast.makeText(this, "Bucket ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bucketRef = FirebaseDatabase.getInstance(firebaseUrl)
            .getReference("buckets").child(bucketId)
        readingsRef = FirebaseDatabase.getInstance(firebaseUrl)
            .getReference("readings").child(bucketId)

        bucketNameTv = findViewById(R.id.bucketNameText)
        levelTv = findViewById(R.id.currentLevelText)
        recycler = findViewById(R.id.historyRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter(data)
        recycler.adapter = adapter

        // Listen for bucket name and current level
        bucketRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown Bucket"
                val level = snapshot.child("latestLevelPercent").getValue(Float::class.java) ?: 0f
                bucketNameTv.text = name
                levelTv.text = "${"%.1f".format(level)}%"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BucketDetailsPageActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        // Observe last 20 readings
        readingsRef.limitToLast(20).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                data.clear()
                for (reading in snapshot.children) {
                    val ts = reading.child("timestamp").getValue(Long::class.java) ?: continue
                    val lvl = reading.child("levelPercent").getValue(Float::class.java) ?: 0f
                    val formattedTime =
                        SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(ts))
                    data.add("$formattedTime  -  ${"%.1f".format(lvl)}%")
                }
                data.reverse()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BucketDetailsPageActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        // Edit button
        findViewById<Button>(R.id.Edit)?.setOnClickListener {
            val intent = Intent(this, EditBucketPageActivity::class.java)
            intent.putExtra("bucketId", bucketId)
            startActivity(intent)
        }
    }

    inner class HistoryAdapter(private val items: List<String>) :
        RecyclerView.Adapter<HistoryAdapter.VH>() {

        inner class VH(val text: TextView) : RecyclerView.ViewHolder(text)

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
            val tv = TextView(parent.context)
            tv.setPadding(16, 16, 16, 16)
            return VH(tv)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.text.text = items[position]
        }
    }
}
