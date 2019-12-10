package com.example.blog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ViewPost : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private var mAdapter: FirebaseRecyclerAdapter<UserModel, PostViewHolder>?=null
    private var mUsers: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)
        val actionBar = supportActionBar
        actionBar!!.title = "View Post"
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)

        val title = intent.getStringExtra("title")
        val body = intent.getStringExtra("content")
        val image = intent.getStringExtra("image")
        val created_by = intent.getStringExtra("posterName")
        val created_at = intent.getStringExtra("Date")


        var posTtitle = findViewById<TextView>(R.id.tv_post_title_show)
        var postCreatedbyName = findViewById<TextView>(R.id.tv_post_created_show)
        var postDatee = findViewById<TextView>(R.id.tv_post_date_created_show)
        var postSelectedBody = findViewById<TextView>(R.id.tv_post_body_show)
        var postForImage = findViewById<ImageView>(R.id.im_post_image_show)
        posTtitle.setText(title)
        postCreatedbyName.setText(created_by)
        postDatee.setText(created_at)
        postSelectedBody.setText(body)

        Glide.with(this)
            .load(image)
            .into(postForImage)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.editpost, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {

        R.id.menu_editpost->{
            editPost()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }

    }
    fun editPost(){
        mAuth= FirebaseAuth.getInstance()

        val userId = mAuth!!.currentUser!!.uid
        val userPostId = intent.getStringExtra("UserId")
        if (userId != userPostId){
            Toast.makeText(this,"Sorry, you didn't post this!",Toast.LENGTH_LONG).show()
        }else{
            val postId = intent.getStringExtra("PostId")
            val postTitle = intent.getStringExtra("title")
            val postBody = intent.getStringExtra("content")
            val postDate = intent.getStringExtra("Date")
            val postImage = intent.getStringExtra("image")

            //Passing values to EditPost Activity

            val i = Intent(this,EditPost::class.java)
            i.putExtra("editPostId",postId)
            i.putExtra("edituserId",userId)
            i.putExtra("editPostTitle",postTitle)
            i.putExtra("editPostContent",postBody)
            i.putExtra("editPostDate",postDate)
            i.putExtra("editPostImage",postImage)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)

        }

    }
}
