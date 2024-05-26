package com.example.smd_project

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavBooksActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var favBooksRef: DatabaseReference
    private lateinit var adapter: adapterForSearchedResults

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_books)
        // Apply night mode based on user's data
        applyNightModeToActivity()




        //*******************************************
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        //*******************************************

        database = FirebaseDatabase.getInstance()
        favBooksRef = database.getReference("favBooksOfReaders/${auth.currentUser?.uid}")

        val favBooksList = ArrayList<BookDataClass>()
        adapter = adapterForSearchedResults(favBooksList)
        val recyclerView = findViewById<RecyclerView>(R.id.FavBooksResultRv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        favBooksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favBooksList.clear()
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(BookDataClass::class.java)
                    book?.let {
                        favBooksList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FavBooksActivity", "Failed to read value.", error.toException())
            }
        })




        var clearAll = findViewById<TextView>(R.id.clear)

        clearAll.setOnClickListener(){
            database = FirebaseDatabase.getInstance()
            favBooksRef = database.getReference("favBooksOfReaders/${auth.currentUser?.uid}")
            favBooksRef.removeValue()
        }




        var booksNvigatorButton = findViewById<ImageView>(R.id.bookLogo)
        booksNvigatorButton.setOnClickListener(){

            var intent = Intent(this, YourBooks::class.java)
            startActivity(intent)


        }
        var searchNvigatorButton = findViewById<ImageView>(R.id.searchButtonNav)
        searchNvigatorButton.setOnClickListener(){


        }




        var personButtonOnNav = findViewById<ImageView>(R.id.personImageOnNav)
        personButtonOnNav.setOnClickListener(){

            var intent = Intent(this, Activity_profile::class.java)
            startActivity(intent)


        }


        var homeButtonOnNav = findViewById<ImageView>(R.id.homeButtonOnNav)

        homeButtonOnNav.setOnClickListener(){

            var intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)

        }





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