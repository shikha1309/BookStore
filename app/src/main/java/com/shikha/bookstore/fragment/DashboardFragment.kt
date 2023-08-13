package com.shikha.bookstore.fragment

import android.content.Context
import   android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shikha.bookstore.R
import com.shikha.bookstore.adapter.DashboardRecyclerAdapter
import com.shikha.bookstore.model.Book
import org.json.JSONObject


class DashboardFragment : Fragment() {
lateinit var recyclerDashboard :RecyclerView
lateinit var layoutManager:RecyclerView.LayoutManager
lateinit var recyclerAdapter: DashboardRecyclerAdapter
lateinit var progressLayout:RelativeLayout
lateinit var progressBar:ProgressBar
val bookInfoList= arrayListOf<Book>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout.visibility=View.VISIBLE
        layoutManager =  LinearLayoutManager (activity)


        //  I am creating  a request for server //
        val  queue = Volley.newRequestQueue( activity as Context)
        val url="http://13.235.250.119/v1/book/fetch_books/"
        val jsonObjectRequest=object: JsonObjectRequest(Request.Method.GET ,url, null ,

            Response.Listener{

              progressLayout.visibility=View.GONE
             val success =it.getBoolean("success")
     if (success){
         val data = it.getJSONArray("data")
         for (i in 0 until data.length()) {
             val bookJsonObject = data.getJSONObject(i)
             val bookObject=Book(

                 bookJsonObject.getString("book_id"),
                 bookJsonObject.getString("name"),
                 bookJsonObject.getString("author"),
                 bookJsonObject.getString("rating"),
                 bookJsonObject.getString("price"),
                 bookJsonObject.getString("image")

             )
             bookInfoList.add(bookObject)  //Add book object to the array list//
             recyclerAdapter=DashboardRecyclerAdapter(activity as Context ,bookInfoList )
             recyclerDashboard.adapter=recyclerAdapter
             recyclerDashboard.layoutManager=layoutManager

         }
     } else{
         Toast.makeText(activity as Context," Some Error Occurred" , Toast.LENGTH_SHORT).show()
     }

      },

            Response.ErrorListener{
              if ( activity!=null){
                  Toast.makeText(activity as Context , " Volley error Occurred" , Toast.LENGTH_SHORT).show()
              }


            })
        {
            override fun getHeaders(): MutableMap<String, String> {
              val headers= HashMap<String, String>()
                headers["Content-type"] ="application/json"
                headers["token"] ="1abec7859238da"
                return headers
            }
        }
        queue.add(jsonObjectRequest)


        return  view
    }


}