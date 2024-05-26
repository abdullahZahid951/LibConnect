package com.example.smd_project

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

import java.util.Locale

class testRecyclerViewForSearch : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: adapterForSearchedResults
    private val searchedBooks: ArrayList<BookDataClass> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recycler_view_for_search)

        recyclerView = findViewById(R.id.notifications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = adapterForSearchedResults(searchedBooks)
        recyclerView.adapter = adapter

        val searchKeyword = "Ellen JAnE" // Your search keyword

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
}
