package com.example.smd_project

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Activity_issuebook : AppCompatActivity() {
    var uid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issuebook)
        val roll=findViewById<EditText>(R.id.searchId)
        val bookid=findViewById<EditText>(R.id.searchTitle)
        val issue=findViewById<Button>(R.id.button3)
        // Apply night mode based on user's data
        applyNightModeToActivity()

        issue.setOnClickListener {
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
                            borrow(uid,bookid.text.toString())
                        }
                    } else {
                        // No user found with the given roll number
                        Toast.makeText(this@Activity_issuebook, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun borrow(uid: String, bookId: String) {
        val allBooksRef = FirebaseDatabase.getInstance().getReference("allBooks").child(bookId)

        // Check if the book exists in allBooks
        allBooksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(allBooksSnapshot: DataSnapshot) {
                if (allBooksSnapshot.exists()) {
                    // Book exists, proceed to borrow
                    val borrowRef = FirebaseDatabase.getInstance().getReference("borrow").child(uid)

                    // Check if the book is already borrowed
                    borrowRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.hasChild(bookId)) {
                                // Book not already borrowed, add it to borrow node
                                val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
                                val currentDate = Date()
                                val formattedDate = dateFormat.format(currentDate)

                                borrowRef.child(bookId).setValue(formattedDate)
                                Toast.makeText(this@Activity_issuebook, "Book Borrowed", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@Activity_issuebook, "Already Borrowed!", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("TAG", "Error checking borrow status: ${databaseError.message}")
                        }
                    })
                } else {
                    // Book not found in allBooks
                    Toast.makeText(this@Activity_issuebook, "No book found", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "Error checking book existence: ${databaseError.message}")
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