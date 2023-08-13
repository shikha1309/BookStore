package com.shikha.bookstore.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.shikha.bookstore.R
import com.shikha.bookstore.activity.DescriptionActivity
import com.shikha.bookstore.model.Book
import com.squareup.picasso.Picasso


class DashboardRecyclerAdapter(val context: Context, val itemList:ArrayList<Book>) :RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder> (){
    //This view will be single line text view that of row//
    class DashboardViewHolder(view: View) :RecyclerView.ViewHolder(view){
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtBookAuthor: TextView = view.findViewById(R.id.txtBookAuthor)
        val txBookPrice: TextView = view.findViewById(R.id.txtBookPrice)
        val txtBookRating: TextView = view.findViewById(R.id.txtBookRating)
        val imgBookImage: ImageView = view.findViewById(R.id.imgBookImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row, parent ,false)
        return DashboardViewHolder(view)

    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txBookPrice.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
//         holder.imgBookImage.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.booktrans).into(holder.imgBookImage)
        holder.llContent.setOnClickListener{
         val intent= Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }
      }

    override fun getItemCount(): Int {
        return itemList.size

    }
}