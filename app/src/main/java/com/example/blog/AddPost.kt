package com.example.blog

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private lateinit var postTitle: EditText
private lateinit var postContent: EditText
private lateinit var postImage: ImageView
private lateinit var name:String
private lateinit var uploadBtn:Button
var contentURI: Uri?= null
private lateinit var database: DatabaseReference
private val GALLERY = 10
private val CAMERA = 21
private var mAuth: FirebaseAuth? = null
private var mDatabase: FirebaseDatabase?=null
private lateinit var hud: KProgressHUD

class AddPost : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        val actionBar = supportActionBar
        actionBar!!.title = "Write Post"
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)

        postImage = findViewById(R.id.bt_addpost_imageupload)
        postImage.setOnClickListener(View.OnClickListener {
            uploadImage()
        })
        uploadBtn = findViewById(R.id.bt_addpost_submit)
        uploadBtn.setOnClickListener(View.OnClickListener {
            uploadPostPix()
        })

    }
    fun addPost(downloadUri:String?){

        //form validation
        postTitle = findViewById(R.id.et_addpost_title)
        postContent = findViewById(R.id.et_addpost_content)
        postImage = findViewById(R.id.bt_addpost_imageupload)


        var title = postTitle.text.toString().trim()
        var content = postContent.text.toString().trim()


        if (title.isEmpty()){
            postTitle.setError("Post Title afield Empty")
        }else if(content.isEmpty()){
            postContent.setError("Post Content Field Empty")
        }else if(contentURI == null){
            Toast.makeText(this, "Post image is required", Toast.LENGTH_LONG).show()
        } else{

            hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Almost done")
                .setDetailsLabel("Post uploading")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
            hud.show()


            //Get the authentication instance
            mAuth = FirebaseAuth.getInstance()

            val userId = mAuth!!.currentUser!!.uid
            //Get the database instance
            mDatabase = FirebaseDatabase.getInstance()
            //fetch the user id of the current User

            val mUsers: DatabaseReference = mDatabase!!.reference.child("Users").child(userId)





            //Retrived current user name  from firebase
            mUsers.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                     name = p0.child("name").value.toString()
                    val ref = FirebaseDatabase.getInstance().getReference("Posts")

//            Get date
                    val date = Calendar.getInstance().time
                    val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
                    val PostDate = formatter.format(date)


                    val post = ref.push().key
                    val postsId = post.toString()

                    val users: UserModel = UserModel(postsId,  title, content, PostDate, name, downloadUri,  userId)
                    ref.child(postsId).setValue(users)
                        .addOnCompleteListener { task ->
                            hud.dismiss()
                            if (task.isSuccessful){

                                Toast.makeText(this@AddPost, "Uploaded Successfully! ", Toast.LENGTH_LONG).show()
                                toMain()

                        } else {
                                Toast.makeText(this@AddPost, "Failed", Toast.LENGTH_LONG).show()
                            }

                        }



                }


            })
//            val ref = FirebaseDatabase.getInstance().getReference("Posts")
//
////            Get date
//            val date = Calendar.getInstance().time
//            val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
//            val PostDate = formatter.format(date)
//
//
//            val post = ref.push().key
//            val postsId = post.toString()
//
//            val users: UserModel = UserModel(postsId,  title, content, PostDate, downloadUri,  userId)
//
//
//            ref.child(postsId).setValue(users)
//                .addOnCompleteListener (this) { task ->
//                    hud.dismiss()
//                    if (task.isSuccessful) {
//                        //fetch current user id
//                        Toast.makeText(this, "Uploaded Successfully! ", Toast.LENGTH_LONG).show()
////
//                    } else {
//                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
//                    }
//                }
//
////            hud.show()



//            hud.show()


        }
    }
    fun uploadImage(){
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
                    postImage = findViewById(R.id.bt_addpost_imageupload)
                    postImage!!.setImageBitmap(bitmap)


//                    Toast.makeText(this@UserProfile, "loaded!", Toast.LENGTH_SHORT).show()

                }
                //catches erros if there is any
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@AddPost, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    fun uploadPostPix(){
        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Verifying image...")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        hud.show()
        val storage = FirebaseStorage.getInstance()
        var storageRef = storage.reference
        if (contentURI == null){
            return
        }

        var file = contentURI
        var pixName = UUID.randomUUID().toString()
        val refim = storageRef.child("images/$pixName")
        val uploadTask = refim.putFile(file!!)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            refim.downloadUrl
        }.addOnCompleteListener { task ->
            hud.dismiss()
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()

                addPost(downloadUri)

            } else {
                // Handle failures

                Toast.makeText(this@AddPost, "Image Upload Failed", Toast.LENGTH_LONG).show()
            }
        }


    }
    fun toMain(){
        val i = Intent(this,MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
    }
//    fun uploadPostPix(){
//        val storage = FirebaseStorage.getInstance()
//        val storageReference = storage.reference
//        val ref = storageReference!!.child("images/"+ UUID.randomUUID().toString())
//        ref.putFile(contentURI!!)
//            .addOnCompleteListener {task->
//                if (task.isSuccessful){
//                    val downloadUri = task.result.toString()
//
//
//                    hud.dismiss()
//                    addPost(downloadUri)
//                    Toast.makeText(this,"Uploaded successful",Toast.LENGTH_LONG).show()
//
//                }else{
//                    Toast.makeText(this,"Failed upload",Toast.LENGTH_LONG).show()
//                }
//            }
//
////            .addOnSuccessListener {
////                ref.downloadUrl
////                 Log.i("name", "${ref.downloadUrl}")
//////
//////                val imgref = FirebaseDatabase.getInstance().getReference("/Users/$userId")
//////                imgref.child("images").push().setValue(ref.downloadUrl)
////                hud.dismiss()
////
////
////                Toast.makeText(this,"Uploaded successful",Toast.LENGTH_LONG).show()
////            }.addOnFailureListener {e ->
////                hud.dismiss()
////
////                Toast.makeText(this,"Uploaded successful" + e.message,Toast.LENGTH_LONG).show()
////            }
//
//
//
//    }

}
