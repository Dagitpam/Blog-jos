package com.example.blog

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditPost : AppCompatActivity() {
    private val GALLERY = 10
    private val CAMERA = 21
    var contentURI: Uri?= null
    private lateinit var profilePicture: ImageView
    private lateinit var titlePost: EditText
    private lateinit var bodyPost: EditText
    private var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private lateinit var hud: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        profilePicture = findViewById(R.id.bt_editpost_imageupload)
        val btnEdit: Button = findViewById(R.id.bt_editpost_submit)
        profilePicture.setOnClickListener(View.OnClickListener {
            //
            takePictures()
        })
        btnEdit.setOnClickListener(View.OnClickListener {
            //
            uploadImageToFireBaseStorage()
        })

        val actionBar = supportActionBar
        actionBar!!.title = "Post Edit"
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)

        //Get the post details from view post activty
        val getTitle = intent.getStringExtra("editPostTitle")
        val getContent = intent.getStringExtra("editPostContent")
        val getImage = intent.getStringExtra("editPostImage")

        val postTitle: EditText = findViewById(R.id.et_editpost_title)
        val postContent: EditText = findViewById(R.id.et_editpost_content)
        val postImage : ImageView = findViewById(R.id.bt_editpost_imageupload)

        postTitle.setText(getTitle)
        postContent.setText(getContent)

        Glide.with(this)
            .load(getImage)
            .into(postImage)



    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun takePictures(){


        //create an object of the alert dialog
        val pictureDialog = AlertDialog.Builder(this)

        // we set out title
        pictureDialog.setTitle("Select Action")

        //we specify the options on this line
        val pictureDialogItems = arrayOf("Select photo from gallery")
        //we set our actions here. if user select any option what should it do
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                //action 1 chooses image from the gallery
                0 -> choosePhotoFromGallary()//this function that performs the action is below
                //action 2 takes a photo from the camera

            }
        }
        //always put this line for the dialog to show
        pictureDialog.show()

    }

    private fun choosePhotoFromGallary() {

        //create an object of an Intent that picks files for you and spcify that it should pick images
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    //After selecting an image from gallery or capturing photo from camera, an onActivityResult() method is executed.
    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        // checks if we picked image from Gallery
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                //gets the image we picked
                contentURI = data.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    //displays the image for us on our image view
                    profilePicture = findViewById(R.id.bt_editpost_imageupload)
                    profilePicture!!.setImageBitmap(bitmap)

//                    uploadImageToFireBaseStorage()
//                    Toast.makeText(this@UserProfile, "loaded!", Toast.LENGTH_SHORT).show()

                }
                //catches erros if there is any
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@EditPost, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }
    fun updatePost(downloadUri:String?){
        titlePost = findViewById(R.id.et_editpost_title)
        bodyPost = findViewById(R.id.et_editpost_content)

        var finaPostTitleEdited = titlePost.text.toString().trim()
        var finalBodyPostEdite = bodyPost.text.toString().trim()

        if (finaPostTitleEdited.isEmpty()){
            titlePost.setError("Post title empty")
        }else if(finalBodyPostEdite.isEmpty()){
            bodyPost.setError("Post content empty")
        }else{

            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
            val PostDate = formatter.format(date)

            val mainPostId = intent.getStringExtra("editPostId")
            val postUpdate = FirebaseDatabase.getInstance().getReference("Posts").child(mainPostId)
            //To check if user didn't update image
            if (downloadUri==null){
                postUpdate!!.child("postTitle").setValue(finaPostTitleEdited)
                postUpdate!!.child("postContent").setValue(finalBodyPostEdite)
                postUpdate!!.child("postDate").setValue(PostDate)
            }else{
                postUpdate!!.child("postTitle").setValue(finaPostTitleEdited)
                postUpdate!!.child("postContent").setValue(finalBodyPostEdite)
                postUpdate!!.child("postDate").setValue(PostDate)
                postUpdate!!.child("postImage").setValue(downloadUri)
            }

//                .addOnCompleteListener (this){task->
//                    if (task.isSuccessful) {
//                        uploadImageToFireBaseStorage()
//                        Toast.makeText(this@EditPost, "Post Updated Successfully!", Toast.LENGTH_LONG).show()
//                    } else {
//                        // Handle failures
//
//                        Toast.makeText(this@EditPost, "Failed!", Toast.LENGTH_LONG).show()
//                    }
//
//                }


        }
    }
    fun uploadImageToFireBaseStorage() {
        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait...")
            .setDetailsLabel("Updating Post")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        hud.show()

        val storage = FirebaseStorage.getInstance()
        var storageRef = storage.reference
        if (contentURI == null) return

        var file = contentURI
        var pixName = UUID.randomUUID().toString()
        val ref = storageRef.child("image/$pixName")
        val uploadTask = ref.putFile(file!!)


        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
//
//                val mainPostId = intent.getStringExtra("mainPostId")
//                val postUpdate = FirebaseDatabase.getInstance().getReference("Posts").child(mainPostId)
//                postUpdate!!.child("image").setValue(downloadUri)
                updatePost(downloadUri)

                hud.dismiss()
                Toast.makeText(this@EditPost, "Post Updated Successfully!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()

            } else {
                // Handle failures

                Toast.makeText(this@EditPost, "Failed!", Toast.LENGTH_LONG).show()
            }
        }
    }

}
