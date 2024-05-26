package com.example.smd_project

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Activity_login : AppCompatActivity() {
    private var connectivityManager: ConnectivityManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        var singUnButtonOnThisView = findViewById<Button>(R.id.signUpButtonOnloginPage)

        singUnButtonOnThisView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        var logInButtonOnThisView = findViewById<Button>(R.id.loginButtonOnLoginPage)
        var emailOfUser =  findViewById<EditText> (R.id.emailEditText)
        var passwordOfUser =  findViewById<EditText> (R.id.passwordEditText)
        emailOfUser.setText("a@gmail.com")
        passwordOfUser.setText("123456789")

        singUnButtonOnThisView.setOnClickListener {

            var intent = Intent(this@Activity_login, Activity_signup::class.java)
            startActivity(intent)
        }
        logInButtonOnThisView.setOnClickListener(){
            connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Call the function to check internet availability
            if (isInternetAvailable()  && emailOfUser.text.isNotEmpty() &&   passwordOfUser.text.isNotEmpty()    ) {
                loginUser(emailOfUser.text.toString(), passwordOfUser.text.toString())
            }
            else {

                Toast.makeText(this, "Net Not Connected", Toast.LENGTH_SHORT).show()


            }


        }


        //**************** Danish Code *********************************
        var guestButton = findViewById<Button>(R.id.buttonForGuestUser)

        guestButton.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        guestButton.setOnClickListener(){
            var intent = Intent(this@Activity_login, GuestHomePage::class.java)
            startActivity(intent)

        }



        //*************************************************************************

    }

    private fun isInternetAvailable(): Boolean {
        var result = false

        // Check if connected to a network
        connectivityManager?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = activeNetwork
                val capabilities = getNetworkCapabilities(network)
                result = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = activeNetworkInfo
                result = networkInfo?.isConnected ?: false
            }
        }

        return result
    }
    private fun loginUser(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if(email=="d@gmail.com") {
                        // Login successful
                        val user: FirebaseUser? = auth.currentUser
                        var intent = Intent(this@Activity_login, Activity_home2::class.java)
                        val uid = user?.uid
                        intent.putExtra("UIDFromLogin", uid)
                        //Toast.makeText(this, uid, Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                    else{
                        // Login successful
                        val user: FirebaseUser? = auth.currentUser
                        var intent = Intent(this@Activity_login, HomePageActivity::class.java)
                        val uid = user?.uid
                        intent.putExtra("UIDFromLogin", uid)
                        //Toast.makeText(this, uid, Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                } else {
                    // If login fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

}