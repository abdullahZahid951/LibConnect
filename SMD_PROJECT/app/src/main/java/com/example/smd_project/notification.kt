package com.example.smd_project

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class notification : AppCompatActivity() {
    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var notificationsAdapter: AdapterForNotifications
    private lateinit var database1: DatabaseReference
    private lateinit var notificationsList: ArrayList<NotificationInfoClass>
    private lateinit var notificationKeys: ArrayList<String>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)




        //*******************************************
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        //*******************************************









        val userId = uid

        database1 = FirebaseDatabase.getInstance().reference

        var clearAllButton = findViewById<TextView>(R.id.clear)

        clearAllButton.setOnClickListener {
            deleteAllNotifications(userId.toString())
        }

        notificationsRecyclerView = findViewById(R.id.notifications)
        notificationsRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationsList = ArrayList()
        notificationKeys = ArrayList()
        notificationsAdapter = AdapterForNotifications(notificationsList)
        notificationsRecyclerView.adapter = notificationsAdapter

        database = FirebaseDatabase.getInstance().reference

        if (userId != null) {
            val userNotificationsRef = database.child("notificationsForReaders").child(userId)
            userNotificationsRef.orderByChild("timestamp").addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val notification = snapshot.getValue(NotificationInfoClass::class.java)
                    if (notification != null) {
                        val key = snapshot.key // Get the key
                        notificationKeys.add(0, key!!) // Store the key
                        notificationsList.add(0, notification)
                        notificationsAdapter.notifyDataSetChanged()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun deleteAllNotifications(uid : String) {
        val database = FirebaseDatabase.getInstance()
        val notificationsRef = database.getReference("notificationsForReaders").child(uid)
        notificationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (key in notificationKeys) {
                    val userNodeRef = notificationsRef.child(key)
                    userNodeRef.removeValue()
                }
                notificationKeys.clear() // Clear the list of keys
                notificationsList.clear() // Clear the list of notifications
                notificationsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error fetching data: ${databaseError.message}")
            }
        })

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
        var notificationInfo = NotificationInfoClass("Notifications Cleared At " + notificationTime
            ,notificationTime ,notificationDate,"",System.currentTimeMillis())
        notificationRefByUniqueVal.setValue(notificationInfo)

        //******************************************************************





    }



}
