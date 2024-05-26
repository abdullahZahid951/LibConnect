package com.example.smd_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterForNotifications(private val notificationsList:ArrayList<NotificationInfoClass>):
    RecyclerView.Adapter<AdapterForNotifications.NotificationListViewHolder>(){

    class NotificationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var notification : TextView = itemView.findViewById(R.id.notificationText)
        var timeTextView : TextView = itemView.findViewById(R.id.time)
        var dateTextView : TextView = itemView.findViewById(R.id.date)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_for_notification, parent, false)
        return NotificationListViewHolder(view)
    }
    override fun getItemCount(): Int {
        return notificationsList.size
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {

        val notificaton = notificationsList[position]
        holder.notification.setText(notificaton.noticationtext)
        holder.dateTextView.setText(notificaton.noticationDate)
        holder.timeTextView.setText(notificaton.noticationTime)


    }


}