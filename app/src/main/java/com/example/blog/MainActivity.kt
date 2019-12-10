package com.example.blog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kaopiz.kprogresshud.KProgressHUD
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {
    private lateinit var usersAdapter: RecyclerView.Recycler
    //    private lateinit var listView : MutableList<User>
    private var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private var mAdapter: FirebaseRecyclerAdapter<UserModel, PostViewHolder>?=null
    private var mUsers: FirebaseDatabase? = null
    private lateinit var usersRecycler: RecyclerView
    private lateinit var hud: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        usersRecycler = findViewById(R.id.all_posts_recycler)



        val mLayoutManager = LinearLayoutManager(applicationContext)

        usersRecycler.layoutManager = mLayoutManager
        usersRecycler.itemAnimator = DefaultItemAnimator()


        loadNotesList()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mainmenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {

        R.id.menu_logout -> {
            logout()
            true
        }
        R.id.menu_profile->{
            profile()
            true
        }
        R.id.menu_addpost->{
            addPost()
            true
        }


        else -> {
            super.onOptionsItemSelected(item)
        }

    }





    fun logout(){
        val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
        mAuth!!.signOut()
        UpdateUserInfo()

    }
    fun profile(){
        val toProfile = Intent(this,Profile::class.java)
        toProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(toProfile)
    }
    fun addPost(){
        val toAddPost = Intent(this,AddPost::class.java)
        toAddPost.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(toAddPost)
    }
    fun UpdateUserInfo(){
        val toLogin = Intent(this,Login::class.java)
        toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(toLogin)
        finish()
    }
    private fun loadNotesList() {

        try {


            hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Fetching Posts")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

            hud.show()

            mAuth = FirebaseAuth.getInstance()
            val userId = mAuth!!.currentUser!!.uid
            database = FirebaseDatabase.getInstance().getReference("Posts")

            val query = database!!.limitToLast(8)
            Log.i("response", "$query")

//        val query = mUsers!!.limitToLast(8)

            mAdapter = object : FirebaseRecyclerAdapter<UserModel, PostViewHolder>(
                UserModel::class.java, R.layout.post_card, PostViewHolder::class.java, query
            ) {

                override fun populateViewHolder(
                    viewHolder: PostViewHolder?,
                    model: UserModel?,
                    position: Int
                ) {


                    viewHolder!!.bindUsers(model)

                }

                override fun onChildChanged(
                    type: ChangeEventListener.EventType,
                    snapshot: DataSnapshot?,
                    index: Int,
                    oldIndex: Int
                ) {
                    super.onChildChanged(type, snapshot, index, oldIndex)
                    usersRecycler.scrollToPosition(index)
                }

            }


            usersRecycler.adapter = mAdapter


            Timer().schedule(timerTask {
                hud.dismiss()
            }, 2000)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
        }
    }
}
