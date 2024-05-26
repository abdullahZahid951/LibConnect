package com.example.smd_project

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class Activity_profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var imageuri: Uri
    private lateinit var user: String
    private var nightmode: String=""
    private lateinit var container: LinearLayout



    private val selectimg = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            imageuri = it
            val img =
                findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
            img.setImageURI(imageuri)
            saveimage()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize borrowLayout after setContentView()
        container = findViewById(R.id.borrowLayout)

        //*******************************************
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        //*******************************************

        container.removeAllViews()

        user=uid.toString()

        val dp=findViewById<LinearLayout>(R.id.edit)
        dp.setOnClickListener {
            selectimg.launch("image/*")
        }


        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user)

// Add a ValueEventListener to listen for changes in the
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                updationOfUserInfo(uid.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Failed to read value.", databaseError.toException())
            }
        })

        updationOfUserInfo(uid.toString())





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
        var edit = findViewById<TextView>(R.id.notifica)

        edit.setOnClickListener(){
            var intent = Intent(this, Activity_editProfile::class.java)
            startActivity(intent)


        }
        var homeButtonOnNav = findViewById<ImageView>(R.id.homeButtonOnNav)

        homeButtonOnNav.setOnClickListener(){

            var intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)

        }

        var previuos = findViewById<Button>(R.id.returnbook)

        previuos.setOnClickListener(){

            var intent = Intent(this, Activity_book_returned::class.java)
            startActivity(intent)

        }

        val database = FirebaseDatabase.getInstance().getReference("borrow").child(uid.toString())

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



    }
    private fun updationOfUserInfo(uid: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        // Add a ValueEventListener to listen for changes in the user's data
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nameTextView = findViewById<TextView>(R.id.name17)
                val rollnum = findViewById<TextView>(R.id.rollnum)
                val fine = findViewById<TextView>(R.id.fine)
                val imageUser = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
                if (dataSnapshot.exists()) {
                    // Data exists, retrieve user information
                    val userInfo = dataSnapshot.getValue(UserInfo::class.java)

                    // Now you can use userInfo to access the user information
                    if (userInfo != null) {
                        val name = userInfo.nameOfNewUser
                        nameTextView.text = name
                        rollnum.text = userInfo.rollNumber
                        fine.text = userInfo.fine.toString()
                        try {
                            nightmode = dataSnapshot.child("night").value.toString()

                            // Set the appropriate night mode
                            val nnnLayout = findViewById<RelativeLayout>(R.id.nnn)
                            if (nightmode == "0") {
                                nnnLayout.setBackgroundColor(Color.parseColor("#A375F4"))
                            }
                            else
                                nnnLayout.setBackgroundColor(Color.parseColor("#302c44"))
                        } catch (e: Exception) {
                            Log.e("TAG", "Error reading night mode status: ${e.message}")
                        }

                        Glide.with(this@Activity_profile)
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
    }
    private fun saveimage() {
        val storeref = FirebaseStorage.getInstance().getReference("Pictures")
            .child(user)
        Log.d("MyTag", "entered save image function")

        storeref.putFile(imageuri!!)
            .addOnSuccessListener {
                storeref.downloadUrl.addOnSuccessListener {
                    //save Path
                    if (user != null) {
                        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
                        databaseRef.child(user).child("image").setValue(it.toString())
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "profile picture updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed to update name: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }
                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }
            }
        Log.d("MyTag", "out of function")
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
                    val linerabooks=itemView.findViewById<LinearLayout>(R.id.bookss)
                    linerabooks.setOnClickListener {

                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(Uri.parse(bookInfoObjHolder!!.downloadUrl), "application/pdf")
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)


                    }


                    msg.text = bookInfoObjHolder?.titleOfTheBook

                    time.text="Click to read pdf"

                    Glide.with(this@Activity_profile)
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
}