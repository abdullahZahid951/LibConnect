package com.example.smd_project

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ComedyBooks : AppCompatActivity() {
    private val searchedBooks: ArrayList<BookDataClass> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: adapterForSearchedResults
    private lateinit var database: FirebaseDatabase
    private lateinit var booksRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comedy_books)
        // Apply night mode based on user's data
        applyNightModeToActivity()

        recyclerView = findViewById(R.id.searchResultRv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = adapterForSearchedResults(searchedBooks)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance()
        booksRef = database.getReference("Comedy")
        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val booksList = ArrayList<BookDataClass>()
                for (snapshot in dataSnapshot.children) {
                    val book = snapshot.getValue(BookDataClass::class.java)
                    book?.let { booksList.add(it) }
                }
                adapter.searchedBooks = booksList
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
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
        val nnnLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.nnn)
        if (nightModeValue == "0") {
            // Apply light mode
            nnnLayout.setBackgroundColor(Color.parseColor("#A375F4"))
        } else {
            // Apply dark mode
            nnnLayout.setBackgroundColor(Color.parseColor("#302c44"))
        }
    }

}
