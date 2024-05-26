package com.example.smd_project

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class Activity_bookreturned : AppCompatActivity() {
    var uid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookreturned)
        val roll=findViewById<EditText>(R.id.searchId)
        val bookid=findViewById<EditText>(R.id.searchTitle)
        val issue=findViewById<Button>(R.id.button3)

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
                            returnBook(uid,bookid.text.toString())
                        }
                    } else {
                        // No user found with the given roll number
                        Toast.makeText(this@Activity_bookreturned, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
        // Apply night mode based on user's data
        applyNightModeToActivity()

    }

    private fun returnBook(uid: String, bookId: String) {
        val borrowRef = FirebaseDatabase.getInstance().getReference("borrow").child(uid).child(bookId)
        borrowRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val borrowedDate = dataSnapshot.getValue(String::class.java) // dd-MM-yy format

                if (borrowedDate != null) {
                    val borrowedDateTime = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).parse(borrowedDate)
                    val currentDateTime = Date()

                    val diffInMillies = currentDateTime.time - borrowedDateTime.time
                    val diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)

                    if (diffInDays >= 7) {
                        // More than or exactly 7 days have passed, add fine
                        addFine(uid)
                    }
                } else {
                    Log.d("returnBook", "Borrowed date not found")
                }

                // Remove the book from the borrow database
                borrowRef.removeValue()

                // Add the book to the "returned" node with the return date as the value of uid
                val returnedRef = FirebaseDatabase.getInstance().getReference("returned").child(uid)
                val returnDate = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Date())
                returnedRef.child(bookId).setValue(returnDate)

                // Show a toast message indicating that the book has been returned successfully
                Toast.makeText(this@Activity_bookreturned, "Book returned successfully", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Show a toast message indicating that no book was found
                Toast.makeText(this@Activity_bookreturned, "No Book found", Toast.LENGTH_SHORT).show()
                Log.e("returnBook", "Error retrieving borrowed date: ${databaseError.message}")
            }
        })
    }


    private fun addFine(uid: String) {
        val fineRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("fine")
        fineRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentFine = dataSnapshot.getValue(Long::class.java) ?: 0

                val newFine = currentFine + 50 // Add 50 as fine
                fineRef.setValue(newFine)
                Log.d("addFine", "Fine added: $newFine")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("addFine", "Error adding fine: ${databaseError.message}")
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