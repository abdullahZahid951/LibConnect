package com.example.smd_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class test_recyclerView1 : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterForTopRatedBooks
    private lateinit var database: FirebaseDatabase
    private lateinit var booksRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recycler_view1)

        recyclerView = findViewById(R.id.topRatedBooks)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = AdapterForTopRatedBooks(ArrayList())
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance()
        booksRef = database.getReference("allBooks")

        // Query the database to get books with rating greater than 5
        val query = booksRef.orderByChild("ratting").startAt(0.0)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val booksList = ArrayList<BookDataClass>()
                for (snapshot in dataSnapshot.children) {
                    val book = snapshot.getValue(BookDataClass::class.java)
                    book?.let { booksList.add(it) }
                }
                adapter.topRatedBooks = booksList
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }





    }
