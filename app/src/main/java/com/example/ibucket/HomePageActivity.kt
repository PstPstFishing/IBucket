package com.example.ibucket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomePageActivity : Activity() {

    data class BucketItem(val id: String, val name: String, val level: Float)

    private lateinit var recyclerView: RecyclerView
    private val bucketList = mutableListOf<BucketItem>()
    private lateinit var adapter: BucketAdapter
    private val firebaseUrl =
        "https://ibucket-e19f3-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // === UI Setup ===
        val emailTextView = findViewById<TextView>(R.id.TextViewEmail)
        val logoutButton = findViewById<Button>(R.id.LogoutButton)
        val addBucketButton = findViewById<Button>(R.id.AddBucketButton)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        recyclerView = findViewById(R.id.bucketRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BucketAdapter(bucketList)
        recyclerView.adapter = adapter

        // === Firebase User ===
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val email = intent.getStringExtra("Email")
        val password = intent.getStringExtra("Password")

        emailTextView.text = "Hello $email"

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // === Load User Buckets ===
        val userBucketsRef = FirebaseDatabase.getInstance(firebaseUrl)
            .getReference("userBuckets").child(uid)

        userBucketsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bucketList.clear()

                for (bucketSnap in snapshot.children) {
                    val bucketId = bucketSnap.key ?: continue

                    // Load bucket info from /buckets
                    val bucketRef = FirebaseDatabase.getInstance(firebaseUrl)
                        .getReference("buckets").child(bucketId)

                    bucketRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnap: DataSnapshot) {
                            val name = dataSnap.child("name").getValue(String::class.java) ?: bucketId
                            val level = dataSnap.child("latestLevelPercent").getValue(Float::class.java) ?: 0f

                            val existing = bucketList.indexOfFirst { it.id == bucketId }
                            if (existing >= 0) {
                                bucketList[existing] = BucketItem(bucketId, name, level)
                            } else {
                                bucketList.add(BucketItem(bucketId, name, level))
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomePageActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        // === Buttons ===
        addBucketButton.setOnClickListener {
            startActivity(Intent(this, SetupBucketPageActivity::class.java))
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginPageActivity::class.java)
            intent.putExtra("Email", email)
            intent.putExtra("Password", password)
            startActivity(intent)
            finish()
        }

        // === Bottom Navigation ===
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfilePageActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsPageActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // === RecyclerView Adapter ===
    inner class BucketAdapter(private val data: List<BucketItem>) :
        RecyclerView.Adapter<BucketAdapter.BucketVH>() {

        inner class BucketVH(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.bucketName)
            val progress: ProgressBar = view.findViewById(R.id.bucketProgress)
            val percentage: TextView = view.findViewById(R.id.bucketPercentage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketVH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.bucket_item, parent, false)
            return BucketVH(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: BucketVH, position: Int) {
            val item = data[position]
            holder.name.text = item.name
            holder.progress.progress = item.level.toInt()
            holder.percentage.text = "${item.level.toInt()}%"

            holder.itemView.setOnClickListener {
                val intent = Intent(this@HomePageActivity, BucketDetailsPageActivity::class.java)
                intent.putExtra("bucketId", item.id)
                startActivity(intent)
            }
        }
    }
}
