package com.example.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class Activity_signup : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        var signUpButtonOnThisView = findViewById<Button>(R.id.signUpButtonOnRegistrationPage)
        var signInButton = findViewById<Button>(R.id.logInButtonOnRegistrationPage)


        signInButton.setOnClickListener(){

            var intent = Intent(this@Activity_signup, Activity_login::class.java)
            startActivity(intent)
            finish()

        }
        signUpButtonOnThisView.setOnClickListener(){

            var nameOfNewUser = findViewById<EditText>(R.id.nameEditText)
            if (nameOfNewUser.text.isEmpty())
            {

                Toast.makeText(this ,"Enter Your Name.", Toast.LENGTH_SHORT)
                    .show()
            }



            var rollNum = findViewById<EditText>(R.id.securityNumberField)
            var rollNumRegix = "^i(0[1-9]|1\\d|2[0-4])-\\d{4}\$".toRegex()
            if (!(rollNumRegix.matches(rollNum.text.toString()))) {
                rollNum.setText("")
                Toast.makeText(this ,"Enter Roll Number In Correct Format.", Toast.LENGTH_SHORT)
                    .show()
            }



            var departmentOfNewUser = findViewById<EditText>(R.id.departmentEditField)
            if (departmentOfNewUser.text.isEmpty())
            {

                Toast.makeText(this ,"Enter Your Department.", Toast.LENGTH_SHORT)
                    .show()
            }



            var emailOfNewUser = findViewById<EditText>(R.id.emailEditText)
            val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            if (!(emailRegex.matches(emailOfNewUser.text.toString()))) {
                emailOfNewUser.setText("")
                Toast.makeText(this ,"Enter Email in correct Format.", Toast.LENGTH_SHORT)
                    .show()
            }



            var passwordOfNewUser = findViewById<EditText>(R.id.newlyEnteredPasswordField)
            if(passwordOfNewUser.length() < 8  )
            {
                passwordOfNewUser.setText("")
                Toast.makeText(this,"Weak Password Or Password Field is Empty", Toast.LENGTH_SHORT).show()
            }


            var confirmPasswordOfNewUser = findViewById<EditText>(R.id.newlyEnteredPasswordField1)


            if(confirmPasswordOfNewUser.text.toString()  != passwordOfNewUser.text.toString() )
            {
                confirmPasswordOfNewUser.setText("")
                Toast.makeText(this,"Password Does Not Match.", Toast.LENGTH_SHORT).show()

            }

            val check1 = findViewById<CheckBox>(R.id.check1)
            val checkBox = check1.isChecked
            if (!checkBox) {
                Toast.makeText(this,"Kindly Tick The Check Box", Toast.LENGTH_SHORT).show()
            }


            if( nameOfNewUser.text.toString().isNotEmpty() && emailOfNewUser.text.toString().isNotEmpty()
                &&rollNum.text.toString().isNotEmpty() &&departmentOfNewUser.text.toString().isNotEmpty() &&
                passwordOfNewUser.text.toString().isNotEmpty() &&
                confirmPasswordOfNewUser.text.toString().isNotEmpty() && checkBox  ){

                registrationOfUser(
                    nameOfNewUser.text.toString(), emailOfNewUser.text.toString(),
                    rollNum.text.toString(),departmentOfNewUser.text.toString() ,passwordOfNewUser.text.toString()  )





            }
        }
    }


    private fun registrationOfUser(
        nameOfNewUser: String,
        emailOfNewUser: String,
        rollNum: String,
        departmentOfNewUser:String,
        passwordOfNewUser:String)
    {
        mAuth = FirebaseAuth.getInstance()
        var uid: String? = ""


        mAuth.createUserWithEmailAndPassword(emailOfNewUser, passwordOfNewUser)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = mAuth.currentUser
                    uid = user?.uid
                    val userInfo = UserInfo(nameOfNewUser,emailOfNewUser,rollNum,departmentOfNewUser,"",0,
                        "https://firebasestorage.googleapis.com/v0/b/smd-project-7976e.appspot.com/o/imagesOfBookCovers%2F1714859163259.jpg?alt=media&token=bd5752c5-d264-46c7-8e1c-250eb113de24")
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("users")


                    //***************** adding notifications in database **************************

                    var noticationRef = FirebaseDatabase.getInstance().getReference("notificationsForReaders")
                    var notificationRefByUid = noticationRef.child(uid!!)
                    var notificationRefByUniqueVal = notificationRefByUid.push()


                    val currentTime = Calendar.getInstance()

                    val hour = currentTime.get(Calendar.HOUR)
                    val minute = currentTime.get(Calendar.MINUTE)
                    val amPM = if (currentTime.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

                    val year = currentTime.get(Calendar.YEAR)
                    val month = currentTime.get(Calendar.MONTH) + 1
                    val dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH)

                    val notificationTime = String.format("%02d:%02d %s", hour, minute, amPM)
                    val notificationDate = String.format("%04d-%02d-%02d", year, month, dayOfMonth)
                    var notificationInfo = NotificationInfoClass("Wellcome New Reader, We'll make sure you won't regret your decision :)"
                            ,notificationTime ,notificationDate,"",System.currentTimeMillis())
                    notificationRefByUniqueVal.setValue(notificationInfo)

                    //******************************************************************

                    //******************* FAV BOOKS INCLUDED ***********************************************
                    var favBookRef = FirebaseDatabase.getInstance().getReference("favBooksOfReaders")
                    var favBookRefByUid = favBookRef.child(uid!!)
                    favBookRefByUid.setValue(0)
                    //**************************************************************************************


                    uid?.let {
                        //******************* Adding Users Data into DataBase **************




                        usersRef.child(uid!!).setValue(userInfo)
                        mAuth.signOut()
                        var intent = Intent(this@Activity_signup, Activity_login::class.java)
                        startActivity(intent)
                        finish()

                    }

                    }else {
                    Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                }


            }



    }

}