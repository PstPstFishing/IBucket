package com.example.ibucket.data

import com.example.ibucket.Model.BucketModel
import com.example.ibucket.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepository {
    private val db = FirebaseDatabase.getInstance(com.google.firebase.FirebaseApp.getInstance()).reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        // Test database connection
        db.child("test").setValue("connected").addOnCompleteListener { task ->
            android.util.Log.d("FirebaseRepository", "Database connection test: ${if(task.isSuccessful) "SUCCESS" else "FAILED"}")
        }
    }

    fun currentUid(): String? = auth.currentUser?.uid

    fun saveUserProfile(user: UserModel, onComplete: (Boolean, Exception?) -> Unit) {
        val uid = user.uid.ifEmpty { currentUid() ?: return onComplete(false, IllegalStateException("No user")) }
        android.util.Log.d("FirebaseRepository", "Saving user profile for uid: $uid")
        db.child("users").child(uid).setValue(user.copy(uid = uid, updatedAt = System.currentTimeMillis()))
            .addOnCompleteListener { onComplete(it.isSuccessful, it.exception) }
    }

    fun getUser(onResult: (UserModel?, Exception?) -> Unit) {
        val uid = currentUid() ?: return onResult(null, IllegalStateException("No user"))
        android.util.Log.d("FirebaseRepository", "Getting user profile for uid: $uid")
        db.child("users").child(uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                android.util.Log.d("FirebaseRepository", "User data retrieved: ${it.result.exists()}")
                onResult(it.result.getValue(UserModel::class.java), null)
            } else {
                android.util.Log.d("FirebaseRepository", "Error getting user: ${it.exception?.message}")
                onResult(null, it.exception)
            }
        }
    }

    fun createBucket(name: String, heightMm: Int, thresholdPercent: Int, onComplete: (String?, Exception?) -> Unit) {
        val uid = currentUid() ?: return onComplete(null, IllegalStateException("No user"))
        val bucketId = db.child("buckets").push().key ?: return onComplete(null, IllegalStateException("No key"))
        android.util.Log.d("FirebaseRepository", "Creating bucket $bucketId for user $uid")
        val now = System.currentTimeMillis()
        val bucket = BucketModel(bucketId, uid, name, heightMm, thresholdPercent, now, now)
        val updates = hashMapOf<String, Any>(
            "/buckets/$bucketId" to bucket,
            "/userBuckets/$uid/$bucketId" to true
        )
        db.updateChildren(updates).addOnCompleteListener { onComplete(if (it.isSuccessful) bucketId else null, it.exception) }
    }

    fun addSimulatedReading(bucketId: String, distanceMm: Int, heightMm: Int, onComplete: (Boolean, Exception?) -> Unit) {
        val level = (((heightMm - distanceMm).coerceAtLeast(0)).toDouble() / heightMm.toDouble() * 100.0).coerceIn(0.0, 100.0)
        val readingRef = db.child("readings").child(bucketId).push()
        val payload = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "distanceMm" to distanceMm,
            "levelPercent" to level
        )
        readingRef.setValue(payload).addOnCompleteListener { onComplete(it.isSuccessful, it.exception) }
    }

    fun observeLatestReading(bucketId: String, onUpdate: (Double?, Exception?) -> Unit) {
        db.child("readings").child(bucketId).limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var level: Double? = null
                    snapshot.children.firstOrNull()?.let { child ->
                        level = child.child("levelPercent").getValue(Double::class.java)
                    }
                    onUpdate(level, null)
                }
                override fun onCancelled(error: DatabaseError) {
                    onUpdate(null, error.toException())
                }
            })
    }

    fun observeReadings(bucketId: String, limit: Int, onUpdate: (List<Pair<Long, Double>>, Exception?) -> Unit) {
        db.child("readings").child(bucketId).limitToLast(limit)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Pair<Long, Double>>()
                    snapshot.children.forEach { child ->
                        val ts = child.child("timestamp").getValue(Long::class.java) ?: return@forEach
                        val lvl = child.child("levelPercent").getValue(Double::class.java) ?: return@forEach
                        list.add(ts to lvl)
                    }
                    list.sortBy { it.first }
                    onUpdate(list, null)
                }
                override fun onCancelled(error: DatabaseError) {
                    onUpdate(emptyList(), error.toException())
                }
            })
    }

    fun getBucket(bucketId: String, onResult: (BucketModel?, Exception?) -> Unit) {
        db.child("buckets").child(bucketId).get().addOnCompleteListener {
            if(it.isSuccessful){
                onResult(it.result.getValue(BucketModel::class.java), null)
            } else {
                onResult(null, it.exception)
            }
        }
    }
}


