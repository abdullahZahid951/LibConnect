package com.example.smd_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class testRecyclerView2 : AppCompatActivity() {
    private lateinit var recyclerView1: RecyclerView
    private lateinit var adapter1: AdapterForTopRatedBooks
    private lateinit var database1: FirebaseDatabase
    private lateinit var booksRef1: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recycler_view2)

        recyclerView1 = findViewById(R.id.randomBooks)
        recyclerView1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter1 = AdapterForTopRatedBooks(ArrayList())
        recyclerView1.adapter = adapter1

        database1 = FirebaseDatabase.getInstance()
        booksRef1 = database1.getReference("allBooks")

        // Add ValueEventListener to listen for changes in the database
        booksRef1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val booksList = ArrayList<BookDataClass>()
                val totalBooks = dataSnapshot.childrenCount.toInt()

                // Calculate the number of books to display (half of total count rounded up)
                val booksToDisplay = (totalBooks + 1) / 2

                // Shuffle the list of books
                val shuffledBooks = dataSnapshot.children.mapNotNull { it.getValue(BookDataClass::class.java) }
                    .shuffled()

                // Select the first half of the shuffled list
                val selectedBooks = ArrayList(shuffledBooks.take(booksToDisplay))

                // Update the RecyclerView adapter
                adapter1.topRatedBooks = selectedBooks
                adapter1.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }
}
