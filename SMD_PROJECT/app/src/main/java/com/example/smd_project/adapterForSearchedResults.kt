package com.example.smd_project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView


class adapterForSearchedResults(var searchedBooks:ArrayList<BookDataClass> ): RecyclerView.Adapter<adapterForSearchedResults.MyViewHolder>() {

    class MyViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {
        val bookImage : ShapeableImageView = itemView.findViewById(R.id.shapeableImageView)
        val title : TextView = itemView.findViewById(R.id.bookTitle)
        val auther : TextView = itemView.findViewById(R.id.auther)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_for_searched_results,parent,false)
        return adapterForSearchedResults.MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return searchedBooks.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookInfo = searchedBooks[position]
        Glide.with(holder.itemView.context)
            .load(bookInfo.downloadBookCoverUrl)
            .into(holder.bookImage)


        holder.title.setText(bookInfo.titleOfTheBook)
        holder.auther.setText(bookInfo.autherOfTheBook)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, BookInfoActivity::class.java)
            intent.putExtra("isbnNum", bookInfo.isbnnum)
            intent.putExtra("typeOfBook", bookInfo.selectedBookGenre)
            holder.itemView.context.startActivity(intent)
        }



    }


}