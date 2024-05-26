package com.example.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GuestHomePage : AppCompatActivity() {


    private lateinit var recyclerView1: RecyclerView
    private lateinit var adapter1: AdapterForTopRatedBooks
    private lateinit var database1: FirebaseDatabase
    private lateinit var booksRef1: DatabaseReference




    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterForTopRatedBooks
    private lateinit var database: FirebaseDatabase
    private lateinit var booksRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_home_page)
        var guestButtonRegister = findViewById<Button>(R.id.regersterGuestUser)
        guestButtonRegister.setOnClickListener(){


            var intent = Intent(this@GuestHomePage, Activity_signup::class.java)
            startActivity(intent)



        }
        ///*********************** RecyclerView Code For Random Books *************************
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


//****************************************************************************************

///*********************** RecyclerView Code For Top Rated Books *************************

        recyclerView = findViewById(R.id.topRatedBooks)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = AdapterForTopRatedBooks(ArrayList())
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance()
        booksRef = database.getReference("allBooks")

        // Query the database to get books with rating greater than
        val query = booksRef.orderByChild("ratting").startAt(3.0)

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


        var searchButton = findViewById<ImageButton>(R.id.searchButton)
        var searchedQuery  = findViewById<EditText>(R.id.searchBarEditText)
        searchButton.setOnClickListener(){

            if(searchedQuery.text.toString() != "") {
                var intent = Intent(this, Activity_search::class.java)
                intent.putExtra("searchedKeyword", searchedQuery.text.toString())
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this , "Enter Search Keyword...", Toast.LENGTH_SHORT).show()
            }

        }

    }

}