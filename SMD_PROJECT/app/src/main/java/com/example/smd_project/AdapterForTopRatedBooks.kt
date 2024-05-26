package com.example.smd_project

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView


class AdapterForTopRatedBooks(var topRatedBooks:ArrayList<BookDataClass> ): RecyclerView.Adapter<AdapterForTopRatedBooks.MyViewHolder>() {
    class MyViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {
  val bookImage : ShapeableImageView = itemView.findViewById(R.id.bookImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_for_top_rated_books,parent,false)
        return AdapterForTopRatedBooks.MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return topRatedBooks.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookInfo = topRatedBooks[position]
        Glide.with(holder.itemView.context)
            .load(bookInfo.downloadBookCoverUrl)
            .into(holder.bookImage)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, BookInfoActivity::class.java)
            intent.putExtra("isbnNum", bookInfo.isbnnum)
            intent.putExtra("typeOfBook", bookInfo.selectedBookGenre)
            holder.itemView.context.startActivity(intent)
        }

    }
}










