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

class Activity_book_returned : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var container: LinearLayout  //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_returned)

        // Initialize borrowLayout after setContentView()
        container = findViewById(R.id.borrowLayout)

        //*******************************************
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        //*******************************************

        // Apply night mode based on user's data
        applyNightModeToActivity()

        container.removeAllViews()

        val database = FirebaseDatabase.getInstance().getReference("returned").child(uid.toString())

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    // Inflate the layout for each child
                    loadBookInfo(childSnapshot.key.toString(),childSnapshot.value.toString())



                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled if needed
            }
        })





        var searchNvigatorButton = findViewById<ImageView>(R.id.searchButtonNav)
        searchNvigatorButton.setOnClickListener(){


        }




        var personButtonOnNav = findViewById<ImageView>(R.id.personImageOnNav)
        personButtonOnNav.setOnClickListener(){

            var intent = Intent(this, Activity_profile::class.java)
            startActivity(intent)


        }
        var favButtonOnNav = findViewById<ImageView>(R.id.favButtonOnNav)

        favButtonOnNav.setOnClickListener(){
            var intent = Intent(this, FavBooksActivity::class.java)
            startActivity(intent)


        }

        var homeButtonOnNav = findViewById<ImageView>(R.id.homeButtonOnNav)

        homeButtonOnNav.setOnClickListener(){

            var intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)

        }




    }
    private fun loadBookInfo(bookId: String, timee: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("allBooks")
        bookId.let { BOOKID ->
            databaseRef.child(BOOKID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookInfoObjHolder = snapshot.getValue(BookDataClass::class.java)

                    val itemView = layoutInflater.inflate(R.layout.list_borrowed, null)

                    val bookImageHolder = itemView.findViewById<ShapeableImageView>(R.id.shapeableImageView)

                    val msg = itemView.findViewById<TextView>(R.id.bookTitle)
                    val time = itemView.findViewById<TextView>(R.id.auther)

                    msg.text = bookInfoObjHolder?.titleOfTheBook

                    time.text=""

                    Glide.with(this@Activity_book_returned)
                        .load(bookInfoObjHolder?.downloadBookCoverUrl)
                        .into(bookImageHolder)

                    container.addView(itemView)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Error reading data from database: ${error.message}")
                }
            })
        }
    }
    private fun applyNightModeToActivity() {
        auth = FirebaseAuth.getInstance()
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