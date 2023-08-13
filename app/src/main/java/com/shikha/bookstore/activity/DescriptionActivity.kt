package com.shikha.bookstore.activity

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shikha.bookstore.R
import com.shikha.bookstore.database.BookDatabase
import com.shikha.bookstore.database.BookEntity
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {


    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var toolbar: Toolbar
    //Storing a book id and giving some random Value//
    var bookId:String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_description)
        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE


        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Book Details"


        if (intent != null){
            bookId = intent.getStringExtra("book_id")
        } else{
            finish()
            Toast.makeText(this@DescriptionActivity, "Some unexpected error" ,Toast.LENGTH_SHORT).show()
        }
        if(bookId=="100") {
            finish()
            Toast.makeText(this@DescriptionActivity, "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
       val url= "http://13.235.250.119/v1/book/get_book/"
        val jsonParams =JSONObject()
        jsonParams.put("book_id",bookId)

        val jsonRequest=object:JsonObjectRequest(Request.Method.POST, url, jsonParams,

            Response.Listener {

                val success=it.getBoolean("success")
                if (success){
                val bookJsonObject=it.getJSONObject("book_data")
                    progressLayout.visibility=View.GONE


                    val bookImageUrl = bookJsonObject.getString("image")
                    //Parsing the Json request//
                    Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.booktrans).into(imgBookImage)
                    txtBookName.text=bookJsonObject.getString("name")
                    txtBookAuthor.text=bookJsonObject.getString("author")
                    txtBookPrice.text = bookJsonObject.getString("price")
                    txtBookRating.text = bookJsonObject.getString("rating")
                    txtBookDesc.text = bookJsonObject.getString("description")

                    // This variable is object of the bookEntity Class

                    val bookEntity = BookEntity(
                        bookId?.toInt() as  Int,
                        txtBookName.text.toString(),
                        txtBookAuthor.text.toString(),
                        txtBookPrice.text.toString(),
                        txtBookRating.text.toString(),
                        txtBookDesc.text.toString(),
                        bookImageUrl
                    )
                    // creating a var and called get method of checkFav functions and this get function will tell that result of the background process is true or not
                    val checkFav = DBAsyncTask(applicationContext ,bookEntity ,1 ) .execute()
                    val isFav=checkFav.get()

                    // if is Fav is true
                    if(isFav){
                        btnAddToFav.text = " Removes from Favourites"
                         val favColor = ContextCompat.getColor(applicationContext , R.color.Green)
                        btnAddToFav.setBackgroundColor(favColor)
                    }
                    else{
                        btnAddToFav.text = "Add to Favourites"
                        val noFavColor=ContextCompat.getColor(applicationContext , R.color.blue_dark)
                        btnAddToFav.setBackgroundColor(noFavColor)

                    }

                    btnAddToFav.setOnClickListener {
                        if (!DBAsyncTask(applicationContext , bookEntity , 1 ).execute().get()){
                            val async =DBAsyncTask(applicationContext ,bookEntity ,2 ).execute()
                            val result =async.get()
                            if (result){
                                Toast.makeText(this@DescriptionActivity,"Book added to favourites",Toast.LENGTH_SHORT).show()
                                btnAddToFav.text ="Remove from favourites"
                                val favColor=ContextCompat.getColor(applicationContext  ,R.color.Green)
                                btnAddToFav.setBackgroundColor(favColor)
                            }
                            else{
                               Toast.makeText(this@DescriptionActivity,"Some Error Occurred!" , Toast.LENGTH_SHORT).show()
                            }

                        }
                        else{
                            val async =DBAsyncTask(applicationContext,bookEntity,3).execute()
                            val result =async.get()
                            if (result){
                                Toast.makeText(this@DescriptionActivity,"Book removed from favourites",Toast.LENGTH_SHORT).show()
                                btnAddToFav.text ="Add to favourites"
                                val noFavColor=ContextCompat.getColor(applicationContext  ,R.color.blue_dark)
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }
                            else{
                                Toast.makeText(this@DescriptionActivity,"Some Error Occurred!" , Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                } else{
                    Toast.makeText(this@DescriptionActivity ,"some Error Occurred" ,Toast.LENGTH_SHORT).show()
                }


        }

            ,Response.ErrorListener{

            } )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "1abec7859238da"
                return headers
            }
        }
queue.add(jsonRequest)

    }

    class DBAsyncTask( val context:Context, val bookEntity: BookEntity,val mode:Int) : AsyncTask<Void, Void, Boolean>(){

        /*
        Mode 1  ->Check DB if the book is favourites or not
         Mode 2 -> Save the book into DB as favourites
         Mode 3 -> Remove the favourites book
         use book Dao for these operations*/

        //Initializing the db class Globally //
        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {


            when (mode) {

                1-> {
                    //Check DB if the book is favourites or not for this we are using book Dao interface for the operations
                    val book:BookEntity?=db.bookDao().getBookBYId(bookEntity.book_id.toString())
                    db.close()
                    return book!=null

                }
                2-> {

                    // Save the book into DB as favourites ,for this we use Insert function
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true

                    // why true because ,there is no chance to null value

                }
                3  -> {

                        db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }

            }
            return false
        }

    }
}