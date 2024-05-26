package com.example.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        auth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference
        val currentUser = auth.currentUser

        Handler(Looper.myLooper()!!).postDelayed({
            if(currentUser != null)
            {
                val uid = currentUser.uid
                if(uid=="rHoOnCcVV6WifzcOpvvuhF23gk42") {
                    var intent = Intent(this@MainActivity, Activity_home2::class.java)
                    startActivity(intent)
                }
                else
                {
                    var intent = Intent(this@MainActivity, HomePageActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
            else
            {

                var intent = Intent(this@MainActivity , Activity_login::class.java )

                startActivity(intent)
                finish()
            }

        }, 3000)//







    }
}