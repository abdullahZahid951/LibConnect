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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.google.firebase.storage.FirebaseStorage

class Activity_home2 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var imageuri: Uri
    private lateinit var user: String
    private var nightmode: String=""



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
        setContentView(R.layout.activity_home2)


        //*******************************************
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        //*******************************************

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
                 fineOfAll()
             }

             override fun onCancelled(databaseError: DatabaseError) {
                 // Handle any errors that occur
                 Log.e("Firebase", "Failed to read value.", databaseError.toException())
             }
         })

         updationOfUserInfo(uid.toString())
         fineOfAll()


         var logoutButtonView = findViewById<TextView>(R.id.logout)
         logoutButtonView.setOnClickListener(){
             auth.signOut()
             Toast.makeText(this , "Logged Out Of Account" , Toast.LENGTH_SHORT ).show()
             var intent = Intent(this@Activity_home2, Activity_login::class.java)
             startActivity(intent)
             finish()
         }





        var edit = findViewById<TextView>(R.id.notifica)

        edit.setOnClickListener(){
            var intent = Intent(this, Activity_editProfile::class.java)
            startActivity(intent)


        }

         val add_book =findViewById<Button>(R.id.admin_addbook)
         add_book.setOnClickListener {
             var intent = Intent(this, ActivityAddbook::class.java)
             startActivity(intent)
         }

         val borrow_book =findViewById<Button>(R.id.book)
         borrow_book.setOnClickListener {
             var intent = Intent(this, Activity_borrowed_books::class.java)
             startActivity(intent)
         }

         val issue_book =findViewById<Button>(R.id.admin_issuebook)
         issue_book.setOnClickListener {
             var intent = Intent(this, Activity_issuebook::class.java)
             startActivity(intent)
         }

         val return_book =findViewById<Button>(R.id.admin_returnbook)
         return_book.setOnClickListener {
             var intent = Intent(this, Activity_bookreturned::class.java)
             startActivity(intent)
         }
         val collect_fine =findViewById<Button>(R.id.admin_fine)
         collect_fine.setOnClickListener {
             var intent = Intent(this, Activity_collectfine::class.java)
             startActivity(intent)
         }


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

                        Glide.with(this@Activity_home2)
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

    private fun fineOfAll() {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalFine = 0

                for (userSnapshot in dataSnapshot.children) {
                    val userInfo = userSnapshot.getValue(UserInfo::class.java)
                    val fine = userInfo?.fine ?: 0
                    totalFine += fine
                }
                val fine=findViewById<TextView>(R.id.fine)
                fine.setText(totalFine.toString())
                Log.d("TotalFine", "Total fine of all users: $totalFine")
                // Now you can display the totalFine wherever you want
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("displayTotalFine", "Error retrieving user data: ${databaseError.message}")
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
}