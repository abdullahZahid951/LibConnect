package com.example.smd_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Activity_editProfile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var nightModeRadioButton: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //*******************************************
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        //*******************************************
        // Initialize Views
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        nightModeRadioButton = findViewById(R.id.night)
        nightModeRadioButton.isChecked = true

        val updatee=findViewById<Button>(R.id.create)
        updatee.setOnClickListener {
            val databaseRef = FirebaseDatabase.getInstance().getReference("users")
            if(!nameEditText.text.isNullOrEmpty()) {
                databaseRef.child(uid.toString()).child("nameOfNewUser")
                    .setValue(nameEditText.text.toString())
            }
            if(!emailEditText.text.isNullOrEmpty()) {
                databaseRef.child(uid.toString()).child("emailOfNewUser")
                    .setValue(emailEditText.text.toString())
            }

            // Finish activity after updating user info
            finish()
        }

        // Set an OnClickListener for the nightModeRadioButton to update the database
        nightModeRadioButton.setOnClickListener {
            val databaseRef = FirebaseDatabase.getInstance().getReference("users")
            if (nightModeRadioButton.isChecked) {
                // Night mode is checked
                databaseRef.child(uid.toString()).child("night").setValue("1")
            } else {
                // Night mode is unchecked
                databaseRef.child(uid.toString()).child("night").setValue("0")
            }
        }

        val back=findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        // Read night mode status from database and update the checkbox state
        readNightModeStatus(uid)
    }

    private fun readNightModeStatus(uid: String?) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
        try {


            databaseRef.child(uid.toString()).child("night")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val nightModeStatus = dataSnapshot.getValue(String::class.java)
                        if (nightModeStatus == "1") {
                            // Night mode is enabled in the database
                            nightModeRadioButton.isChecked = true
                        } else {
                            // Night mode is disabled in the database
                            nightModeRadioButton.isChecked = false
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors here
                        Log.e("TAG", "Error reading night mode status: ${databaseError.message}")
                    }
                })
        }
        catch (e: Exception){}
    }
}
