package com.example.smd_project

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomePageActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var database3: DatabaseReference


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
        setContentView(R.layout.activity_home_page)

        //*******************************************
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        //*******************************************


        Log.d("Uid" , uid.toString() )


        updationOfUserInfo(uid.toString())

        var AiBasedButton = findViewById<ImageView>(R.id.imageView2)

        AiBasedButton.setOnClickListener(){
            var intent = Intent(this@HomePageActivity, Activity_AiRecommendation::class.java)
            startActivity(intent)
        }











        var notificationButton = findViewById<ImageView>(R.id.bell_with_notification)

        notificationButton.setOnClickListener(){

            var intent = Intent(this@HomePageActivity, notification::class.java)
            startActivity(intent)


        }

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
                Toast.makeText(this , "Enter Search Keyword...",Toast.LENGTH_SHORT).show()
            }

        }


        var logoutButtonView = findViewById<ImageView>(R.id.logoutButton)
        logoutButtonView.setOnClickListener(){
            auth.signOut()
            Toast.makeText(this , "Logged Out Of Account" , Toast.LENGTH_SHORT ).show()
            var intent = Intent(this@HomePageActivity, Activity_login::class.java)
            startActivity(intent)
            finish()
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





    }


//**************************************************************************

    private fun updationOfUserInfo(uid: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        // Add a ValueEventListener to listen for changes in the user's data
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nameTextView = findViewById<TextView>(R.id.textView)
                var imageUser = findViewById<ImageView>(R.id.userImage)
                if (dataSnapshot.exists()) {
                    // Data exists, retrieve user information
                    val userInfo = dataSnapshot.getValue(UserInfo::class.java)

                    try {
                        val nightmode = dataSnapshot.child("night").value.toString()

                        // Set the appropriate night mode
                        val nnnLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.nnn)
                        if (nightmode == "0") {
                            nnnLayout.setBackgroundColor(Color.parseColor("#A375F4"))
                        }
                        else
                            nnnLayout.setBackgroundColor(Color.parseColor("#302c44"))
                    } catch (e: Exception) {
                        Log.e("TAG", "Error reading night mode status: ${e.message}")
                    }
                    // Now you can use userInfo to access the user information
                    if (userInfo != null) {
                        val name = userInfo.nameOfNewUser
                        nameTextView.text = "Hello " + name + " !!"
                        Log.d("s",userInfo.image)
                        Glide.with(this@HomePageActivity)
                            .load(userInfo.image)
                            .apply(RequestOptions().transform(CircleCrop()))
                            .placeholder(R.drawable.image1) // Placeholder image while loading
                            .error(R.drawable.image1) // Error image if loading fails
                            .into(imageUser)

                    }
                } else {
                    // Data doesn't exist or user not found
                    Log.w("TAG", "User data does not exist for UID: $uid")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                Log.e("TAG", "Error retrieving user data for UID: $uid", databaseError.toException())
            }
        })

///**************************** Static Horizontal Scrool View **********************

        var sHISTORY = findViewById<ImageView>(R.id.imageg1)
        sHISTORY.setOnClickListener(){
            var intent = Intent(this@HomePageActivity, HistoryBooks::class.java)
            startActivity(intent)


        }

        var crimeButton = findViewById<ImageView>(R.id.crimeButton)
        crimeButton.setOnClickListener(){
            var intent = Intent(this@HomePageActivity, CrimeBooks::class.java)
            startActivity(intent)
        }
        var comedyButton = findViewById<ImageView>(R.id.comedyButton)
        comedyButton.setOnClickListener(){

            var intent = Intent(this@HomePageActivity, ComedyBooks::class.java)
            startActivity(intent)

        }
        var fictionButton = findViewById<ImageView>(R.id.fictionButton)
        fictionButton.setOnClickListener(){

            var intent = Intent(this@HomePageActivity, FictionBooks::class.java)
            startActivity(intent)


        }


        //******************************************************************




    }

}
