package com.example.smd_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class testRecyclerViewForNotifications : AppCompatActivity() {
    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var notificationsAdapter: AdapterForNotifications
    private lateinit var notificationsList: ArrayList<NotificationInfoClass>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recycler_view_for_notifications)


        notificationsRecyclerView = findViewById(R.id.notifications)
        notificationsRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationsList = ArrayList()
        notificationsAdapter = AdapterForNotifications(notificationsList)
        notificationsRecyclerView.adapter = notificationsAdapter

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference

///**********************Yahan Uid Agaye gii ************--------------------
        val userId = "TLSaxlgSwMaQTAqTMPBhoge7NYI3"

//********************************************************************
        if (userId != null) {
            val userNotificationsRef = database.child("notificationsForReaders").child(userId)
            userNotificationsRef.orderByChild("timestamp").addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val notification = snapshot.getValue(NotificationInfoClass::class.java)
                    if (notification != null) {
                        notificationsList.add(0, notification) // Add new notification at the beginning of the list (to display the most recent one first)
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

















}
