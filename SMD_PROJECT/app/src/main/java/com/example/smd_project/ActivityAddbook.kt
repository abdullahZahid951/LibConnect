package com.example.smd_project

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import java.util.Calendar

class ActivityAddbook : AppCompatActivity() {
    private val storage = Firebase.storage
    private val imageRef = storage.reference.child("imagesOfBookCovers")

    private var selectedImageUri: Uri? = null
    private var downloadUrl : String = ""
    private var downloadBookCoverUrl : String = ""

    private val PICK_FILE_REQUEST_CODE = 1
    private val MIME_TYPE = "application/pdf"

    private val firebaseStorage = Firebase.storage
    private val storageReference = firebaseStorage.reference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbook)

        // Apply night mode based on user's data
        applyNightModeToActivity()




        var ISBNNum =  findViewById<EditText> (R.id.searchId)

        var titleOfTheBook =  findViewById<EditText> (R.id.searchTitle)

        var bookCatagorySpinner = findViewById<Spinner>(R.id.bookCatagorySpinner)

        var autherOfTheBook =  findViewById<EditText> (R.id.auther)

        var synopsis =  findViewById<EditText> (R.id.synopsis)



        val imageVar = findViewById<ShapeableImageView>(R.id.bookHolderImage)
        imageVar.setOnClickListener(){
            openGallery()
            imageVar.setImageURI(selectedImageUri)

        }

        var bookFile = findViewById<TextView>(R.id.bookFileHolder)

        bookFile.setOnClickListener(){

            openFilePicker()
            Log.d("File "  , downloadUrl )

        }










        var addBookButtonOnThisView = findViewById<Button>(R.id.addBook)

        addBookButtonOnThisView.setOnClickListener(){

            val selectedBookGenre = bookCatagorySpinner.selectedItem?.toString()

            if(ISBNNum.text.toString().isEmpty()) {
                Toast.makeText(this@ActivityAddbook, "Enter ISBN Number.", Toast.LENGTH_SHORT).show()

            }
            if(titleOfTheBook.text.toString().isEmpty()) {
                Toast.makeText(this@ActivityAddbook, "Enter Book Title", Toast.LENGTH_SHORT).show()

            }

            if(autherOfTheBook.text.toString().isEmpty()) {
                Toast.makeText(this@ActivityAddbook, "Enter Book Title", Toast.LENGTH_SHORT).show()

            }
            if(downloadUrl.isEmpty()) {
                Toast.makeText(this@ActivityAddbook, "Attach Book's PDF", Toast.LENGTH_SHORT).show()
            }


            if(!downloadUrl.isEmpty()  && !autherOfTheBook.text.toString().isEmpty() &&
                !titleOfTheBook.text.toString().isEmpty() && !ISBNNum.text.toString().isEmpty()
                &&!synopsis.text.toString().isEmpty())
            {
                Log.d("Check" , selectedBookGenre.toString() )
                if (downloadBookCoverUrl.isEmpty())
                {
                    downloadBookCoverUrl = "https://firebasestorage.googleapis.com/v0/b/smd-project-7976e.appspot.com/o/imagesOfBookCovers%2F1714859163259.jpg?alt=media&token=bd5752c5-d264-46c7-8e1c-250eb113de24"
                }

                addNewBook(titleOfTheBook.text.toString(),autherOfTheBook.text.toString(),
                    ISBNNum.text.toString(),downloadBookCoverUrl,downloadUrl,selectedBookGenre.toString(),
                    synopsis.text.toString())

            }




        }




    }
    private fun addNewBook(titleOfTheBook:String,autherOfTheBook:String,ISBNNum:String,
                           downloadBookCoverUrl:String,downloadUrl:String,selectedBookGenre:String,
                           synopsis:String)
    {

        val BookInfo = BookDataClass(titleOfTheBook,autherOfTheBook,ISBNNum,downloadBookCoverUrl,
            downloadUrl,selectedBookGenre,System.currentTimeMillis(),0,synopsis)

        val database = FirebaseDatabase.getInstance()
        val booksRef = database.getReference("allBooks")
        booksRef.child(ISBNNum!!).setValue(BookInfo)
        Toast.makeText(this@ActivityAddbook, "Book Addition Completed", Toast.LENGTH_SHORT).show()


        //************* Creation Of Notification Of Books To all users ***


        spreadNotificationToAllOurReaders(ISBNNum,titleOfTheBook,autherOfTheBook)


        ///***************************************************************************************

        //**********************************************************************
        if(selectedBookGenre == "Comedy" )
        {
            val database = FirebaseDatabase.getInstance()
            val booksRef = database.getReference("Comedy")
            booksRef.child(ISBNNum!!).setValue(BookInfo)

        }
        else if(selectedBookGenre == "History" )
        {
            val database = FirebaseDatabase.getInstance()
            val booksRef = database.getReference("History")
            booksRef.child(ISBNNum!!).setValue(BookInfo)

        }
        else if(selectedBookGenre == "Fiction" )
        {
            val database = FirebaseDatabase.getInstance()
            val booksRef = database.getReference("Fiction")
            booksRef.child(ISBNNum!!).setValue(BookInfo)

        }
        else if(selectedBookGenre == "Crime" )
        {
            val database = FirebaseDatabase.getInstance()
            val booksRef = database.getReference("Crime")
            booksRef.child(ISBNNum!!).setValue(BookInfo)

        }



    }
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = MIME_TYPE
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadFileToFirebaseStorage(uri)
            }
        }
    }

    private fun uploadFileToFirebaseStorage(fileUri: Uri) {
        val fileName = fileUri.lastPathSegment ?: "file"
        val folderName = "booksPdf"
        val fileReference = storageReference.child("$folderName/$fileName")

        fileReference.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                     downloadUrl = uri.toString()
                    // Handle the download URL (e.g., save to database)
                    // For now, you can print it
                    println("Download URL: $downloadUrl")

                    Toast.makeText(this , "File Updated Successfully" , Toast.LENGTH_SHORT   ).show()

                }
            }
            .addOnFailureListener { exception ->
                println("File upload failed: $exception")
            }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                selectedImageUri = uri
                uploadImageToFirebaseStorage()
            }
        }
    }
    private fun uploadImageToFirebaseStorage() {
        selectedImageUri?.let { uri ->
            val imageRef = storage.reference.child("imagesOfBookCovers/${System.currentTimeMillis()}.jpg")

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        downloadBookCoverUrl = imageUrl
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        // Handle failure
                    }
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }


    private fun spreadNotificationToAllOurReaders(isbn : String , title: String ,auther : String ) {
        val database = FirebaseDatabase.getInstance()
        val notificationsRef = database.getReference("notificationsForReaders")

        notificationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val uid = childSnapshot.key

                    // *********** Telling Users About The Books *********************

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
                    var notificationInfo = NotificationInfoClass("{ "+title + " } From { " + auther + " } is uploaded. Read And Enjoy..."
                        ,notificationTime ,notificationDate,isbn,System.currentTimeMillis())
                    notificationRefByUniqueVal.setValue(notificationInfo)





                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error fetching data: ${databaseError.message}")
            }
        })




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
        val nnnLayout = findViewById<RelativeLayout>(R.id.nnn)
        if (nightModeValue == "0") {
            // Apply light mode
            nnnLayout.setBackgroundColor(Color.parseColor("#A375F4"))
        } else {
            // Apply dark mode
            nnnLayout.setBackgroundColor(Color.parseColor("#302c44"))
        }
    }


}