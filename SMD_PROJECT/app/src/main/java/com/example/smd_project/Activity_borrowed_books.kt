package com.example.smd_project

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class Activity_borrowed_books : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var container: LinearLayout
    private val allBooksMap: MutableMap<String, Int> = mutableMapOf() // Map to hold bookId and borrowed count

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrowed_books)
        // Apply night mode based on user's data
        applyNightModeToActivity()

        container = findViewById(R.id.borrowLayout)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        container.removeAllViews()

        val database = FirebaseDatabase.getInstance().getReference("borrow")
        val allBooksRef = FirebaseDatabase.getInstance().getReference("allBooks")

        // Fetching all books from "allBooks" node to populate the allBooksMap
        allBooksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(allBooksSnapshot: DataSnapshot) {
                allBooksSnapshot.children.forEach { bookSnapshot ->
                    allBooksMap[bookSnapshot.key.toString()] = 0 // Initialize borrowed count to 0 for each book
                }

                // Fetching borrowed books from "borrow" node
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.forEach { childSnapshot ->
                            childSnapshot.children.forEach { bookSnapshot ->
                                val bookId = bookSnapshot.key.toString()
                                val borrowedCount = allBooksMap[bookId] ?: 0 // Get borrowed count for this book
                                allBooksMap[bookId] = borrowedCount + 1 // Increment borrowed count for this book
                            }
                        }

                        // After fetching borrowed counts, load book info
                        allBooksMap.forEach { (bookId, borrowedCount) ->
                            loadBookInfo(bookId, borrowedCount.toString())
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle onCancelled if needed
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled if needed
            }
        })


    }

    private fun loadBookInfo(bookId: String, borrowedCount: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("allBooks")
        databaseRef.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookInfoObjHolder = snapshot.getValue(BookDataClass::class.java)
                val itemView = layoutInflater.inflate(R.layout.list_borrowed, null)
                val bookImageHolder = itemView.findViewById<ShapeableImageView>(R.id.shapeableImageView)
                val msg = itemView.findViewById<TextView>(R.id.bookTitle)
                val time = itemView.findViewById<TextView>(R.id.auther)

                msg.text = bookInfoObjHolder?.titleOfTheBook

                // Display borrowed count
                time.text = borrowedCount

                Glide.with(this@Activity_borrowed_books)
                    .load(bookInfoObjHolder?.downloadBookCoverUrl)
                    .into(bookImageHolder)

                container.addView(itemView)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Error reading data from database: ${error.message}")
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
        val nnnLayout = findViewById<LinearLayout>(R.id.nnn)
        if (nightModeValue == "0") {
            // Apply light mode
            nnnLayout.setBackgroundColor(Color.parseColor("#A375F4"))
        } else {
            // Apply dark mode
            nnnLayout.setBackgroundColor(Color.parseColor("#302c44"))
        }
    }

}
