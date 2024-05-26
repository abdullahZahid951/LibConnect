package com.example.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Activity_AiRecommendation : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var favBooksRef: DatabaseReference
    private lateinit var allBooksRef: DatabaseReference
    private lateinit var database1: FirebaseDatabase


    private lateinit var database3: FirebaseDatabase
    private lateinit var favBooksRef3: DatabaseReference
    private lateinit var adapter3: adapterForSearchedResults


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_recommendation)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        database1 = FirebaseDatabase.getInstance()

        // Get current user ID
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        // Firebase references
        favBooksRef = database.getReference("favBooksOfReaders/$uid")
        allBooksRef = database1.getReference("allBooks")

        // Event listener to fetch all books and favorite books
        allBooksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(allBooksSnapshot: DataSnapshot) {
                val allBooksList = ArrayList<BookDataClass>()
                for (bookSnapshot in allBooksSnapshot.children) {
                    val book = bookSnapshot.getValue(BookDataClass::class.java)
                    book?.let {
                        allBooksList.add(it)
                    }
                }

                favBooksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(favBooksSnapshot: DataSnapshot) {
                        val favBooksList = ArrayList<BookDataClass>()
                        for (bookSnapshot in favBooksSnapshot.children) {
                            val book = bookSnapshot.getValue(BookDataClass::class.java)
                            book?.let {
                                favBooksList.add(it)
                            }
                        }

                        // Send data to recommendation function
                        recommendBooks(allBooksList, favBooksList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FavBooksActivity", "Failed to read value.", error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AllBooksActivity", "Failed to read value.", error.toException())
            }
        })

        // Button listeners
        val booksNavigatorButton = findViewById<ImageView>(R.id.bookLogo)
        booksNavigatorButton.setOnClickListener(){
            val intent = Intent(this, YourBooks::class.java)
            startActivity(intent)
        }

        val searchNavigatorButton = findViewById<ImageView>(R.id.searchButtonNav)
        searchNavigatorButton.setOnClickListener(){
            // Add your search functionality here
        }

        val favButtonOnNav = findViewById<ImageView>(R.id.favButtonOnNav)
        favButtonOnNav.setOnClickListener(){
            val intent = Intent(this, FavBooksActivity::class.java)
            startActivity(intent)
        }

        val personButtonOnNav = findViewById<ImageView>(R.id.personImageOnNav)
        personButtonOnNav.setOnClickListener(){
            val intent = Intent(this, Activity_profile::class.java)
            startActivity(intent)
        }
    }

    private fun recommendBooks(allBooksList: List<BookDataClass>, favBooksList: List<BookDataClass>): List<Triple<String, String, Double>> {
        val neuralNet = NeuralNetwork(learningRate = 0.01, lambda = 0.01)
        neuralNet.train(favBooksList)
        val sortedBooks = neuralNet.predictAndSort(allBooksList)

        // Shuffle the sorted books
        val shuffledBooks = sortedBooks.shuffled()

        // Select only half of the shuffled books
        val halfCount = shuffledBooks.size / 2
        val selectedBooks = shuffledBooks.take(halfCount)

        val recommendedBooks = mutableListOf<Triple<String, String, Double>>()

        database3 = FirebaseDatabase.getInstance()
        favBooksRef3 = database3.getReference("allBooks")
        val favBooksList = ArrayList<BookDataClass>()
        adapter3 = adapterForSearchedResults(favBooksList)
        val recyclerView = findViewById<RecyclerView>(R.id.aiBasedResults)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter3


        selectedBooks.forEach { (bookai, score) ->

            favBooksRef3.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (bookSnapshot in snapshot.children) {
                        val book = bookSnapshot.getValue(BookDataClass::class.java)

                        if(bookai.isbnnum == book?.isbnnum  ){
                            book?.let {
                                favBooksList.add(it)
                            } }
                    }

                    adapter3.notifyDataSetChanged()


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FavBooksActivity", "Failed to read value.", error.toException())
                }


            })









            recommendedBooks.add(Triple(bookai.isbnnum, bookai.titleOfTheBook, score))
            Log.d("RecommendedBooks", "Title: ${bookai.titleOfTheBook}, Author: ${bookai.autherOfTheBook}, ISBN: ${bookai.isbnnum}, Predicted Popularity: $score")
        }

        return recommendedBooks
    }}