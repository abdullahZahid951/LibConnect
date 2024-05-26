package com.example.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterForTopRatedBooks
    private lateinit var database: FirebaseDatabase
    private lateinit var booksRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)

        var bookId :  String = ""
        var Genre :  String = ""
        val intent = intent
        bookId = intent.getStringExtra("isbnNum").toString()
        Genre = intent.getStringExtra("typeOfBook").toString()


        //*******************************************
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        //*******************************************

//        Toast.makeText(this,Genre,Toast.LENGTH_LONG).show()

         loadBookInfo(bookId,uid.toString())
        //************************* Similar Books *******************************
        recyclerView = findViewById(R.id.topRatedBooks)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = AdapterForTopRatedBooks(ArrayList())
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance()
        booksRef = database.getReference(Genre)

        booksRef.addValueEventListener(object : ValueEventListener {
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

        var booksNvigatorButton = findViewById<ImageView>(R.id.bookLogo)
        booksNvigatorButton.setOnClickListener(){

            var intent = Intent(this, YourBooks::class.java)
            startActivity(intent)


        }
        var searchNvigatorButton = findViewById<ImageView>(R.id.searchButtonNav)
        searchNvigatorButton.setOnClickListener(){


        }


        var favButtonOnNav = findViewById<ImageView>(R.id.favButtonOnNav)

        favButtonOnNav.setOnClickListener(){
            var intent = Intent(this, FavBooksActivity::class.java)
            startActivity(intent)


        }

        var personButtonOnNav = findViewById<ImageView>(R.id.personImageOnNav)
        personButtonOnNav.setOnClickListener(){

            var intent = Intent(this, Activity_profile::class.java)
            startActivity(intent)


        }

        var borrow=findViewById<Button>(R.id.borrow)
        borrow.setOnClickListener {
            val database = FirebaseDatabase.getInstance().getReference("borrow").child(uid.toString())

            val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
            val currentDate = Date()
            val formattedDate = dateFormat.format(currentDate)


            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var bookIdFound = false
                    Log.d("datasnap", dataSnapshot.toString())
                    Log.d("book", bookId)
                    for (childSnapshot in dataSnapshot.children) {
                        val bookIdFromDatabase = childSnapshot.getValue(String::class.java) // Assuming bookId is stored as String
                        Log.d("childsnap", childSnapshot.key.toString())
                        if (bookId.toString() ==childSnapshot.key.toString()) {
                            bookIdFound = true
                            break
                        }
                    }

                    if (bookIdFound==false) {
                        database.child(bookId.toString()).push()

                        //add date
                        val db = FirebaseDatabase.getInstance().getReference("borrow").child(uid.toString())
                            .child(bookId.toString())
                        db.setValue(formattedDate.toString())
                        Toast.makeText(this@BookInfoActivity, "Book Borrowed", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        Toast.makeText(this@BookInfoActivity, "Already Borrowed!", Toast.LENGTH_LONG).show()
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //println("Error: ${databaseError.toException()}")
                }
            })



        }


    }

    private fun loadBookInfo(bookId : String,uid:String){
        val databaseRef = FirebaseDatabase.getInstance().getReference("allBooks")
        bookId.let {BOOKID ->
            databaseRef.child(BOOKID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookInfoObjHolder = snapshot.getValue(BookDataClass::class.java)

                   var bookImageHolder = findViewById<ShapeableImageView>(R.id.shapeableImageView)
                    var bookNameHolder = findViewById<TextView>(R.id.bookName)
                    var autherNameHolder = findViewById<TextView>(R.id.authorName)
                    var bookSynopsis = findViewById<TextView>(R.id.synopsis)

                   var favButton = findViewById<ImageView>(R.id.imageView)
                    favButton.setOnClickListener() {
                        var favBookRef =
                            FirebaseDatabase.getInstance().getReference("favBooksOfReaders")
                        var favBookRefByUid = favBookRef.child(uid!!).child(BOOKID)
                        favBookRefByUid.setValue(bookInfoObjHolder)

                    }

                    bookNameHolder.setText(bookInfoObjHolder?.titleOfTheBook)
                    autherNameHolder.setText(bookInfoObjHolder?.autherOfTheBook)
                    bookSynopsis.setText(bookInfoObjHolder?.synopsis)

                    Glide.with(this@BookInfoActivity)
                        .load(bookInfoObjHolder?.downloadBookCoverUrl)
                        .into(bookImageHolder)



                }
                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Log.e("TAG", "Error reading data from database: ${error.message}")
                }


            })




        }

    }

}