package com.example.smd_project

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class Activity_search : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: adapterForSearchedResults
    private val searchedBooks: ArrayList<BookDataClass> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        recyclerView = findViewById(R.id.searchResultRv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = adapterForSearchedResults(searchedBooks)
        recyclerView.adapter = adapter
        // Apply night mode based on user's data
        applyNightModeToActivity()



        val intent = intent
        val s = intent.getStringExtra("searchedKeyword")
        var searchKeyword : String = s.toString()
        Log.d("s",searchKeyword)
        val database = FirebaseDatabase.getInstance().reference.child("allBooks")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                searchedBooks.clear()
                var booksFound = false // Flag to track if books are found

                for (snapshot in dataSnapshot.children) {
                    val book = snapshot.getValue(BookDataClass::class.java)
                    if (book != null) {
                        val author = book.autherOfTheBook.lowercase(Locale.getDefault())
                        val title = book.titleOfTheBook.lowercase(Locale.getDefault())
                        val keyword = searchKeyword.lowercase(Locale.getDefault())
                        if (author.contains(keyword) || title.contains(keyword)) {
                            searchedBooks.add(book)
                            booksFound = true // Set the flag to true if at least one book is found
                        }
                    }
                }
                adapter.notifyDataSetChanged()

                if (!booksFound) {
                    Log.d("Search", "No books found for keyword: $searchKeyword")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Database", "Error fetching data: ${databaseError.message}")
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


