package com.example.smd_project

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Activity_collectfine : AppCompatActivity() {
    var uid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collectfine)
        // Apply night mode based on user's data
        applyNightModeToActivity()

        val roll=findViewById<EditText>(R.id.searchId)
        val amount=findViewById<EditText>(R.id.searchTitle)
        val collect=findViewById<Button>(R.id.button3)

        collect.setOnClickListener {
            val usersRef = FirebaseDatabase.getInstance().getReference("users")         //finding userid from roll num
            val query = usersRef.orderByChild("rollNumber").equalTo(roll.text.toString())

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Loop through the result set, although it should be just one user
                        for (userSnapshot in dataSnapshot.children) {
                            val userId = userSnapshot.key // This will give you the user ID
                            Log.d("TAG----", userId.toString())
                            uid=userId.toString()
                            collecFine(uid,amount.text.toString())
                        }
                    } else {
                        // No user found with the given roll number
                        Toast.makeText(this@Activity_collectfine, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun collecFine(uid: String, amount: String) {
        val fineRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("fine")
        fineRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentFine = dataSnapshot.getValue(Long::class.java) ?: 0
                val fineAmount = amount.toLong() // Convert the amount String to Long

                // Check if the fine amount entered is not more than the total fine
                if (fineAmount <= currentFine) {
                    val newFine = currentFine - fineAmount // Subtract the collected fine from the current fine
                    fineRef.setValue(newFine)

                    // Show a toast message indicating that the fine has been collected
                    Toast.makeText(this@Activity_collectfine, "Fine collected successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Show a toast message indicating that the fine amount entered is more than the total fine
                    Toast.makeText(this@Activity_collectfine, "Fine is more than total fine", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("collecFine", "Error collecting fine: ${databaseError.message}")
            }
        })
    }
    private fun applyNightModeToActivity() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid.toString())
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val nightModeValue = dataSnapshot.child("night").value.toString()
                    applyNightMode(nightModeValue)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    private fun applyNightMode(nightModeValue: String) {
        val nnnLayout = findViewById<RelativeLayout>(R.id.nnn)
        if (nightModeValue == "0") {
            // Apply light mode
            nnnLayout.setBackgroundColor(Color.parseColor("#A375F4"))
        } else {
            // Apply dark mode
            nnnLayout.setBackgroundColor(Color.parseColor("#302c44"))
        }
    }



}