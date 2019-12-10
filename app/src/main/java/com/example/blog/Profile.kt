package com.example.blog

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kaopiz.kprogresshud.KProgressHUD
import java.io.IOException
import java.util.*

class Profile : AppCompatActivity() {
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var userId: String
    private  lateinit var contentURI:Uri
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var take_picture: ImageView
    private lateinit var picture_display: ImageView
    private lateinit var picture: ImageView
    private lateinit var hud: KProgressHUD

    private val GALLERY = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val actionBar = supportActionBar
        actionBar!!.title = "Profile"
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)

        //Get the authentication instance
        mAuth = FirebaseAuth.getInstance()
        //Get the database instance
        mDatabase = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()
        //fetch the user id of the current User
        userId = mAuth!!.currentUser!!.uid

        val mUsers: DatabaseReference = mDatabase!!.reference.child("Users").child(userId)

        val name: TextView = findViewById(R.id.tv_profile_name)
        val email: TextView = findViewById(R.id.tv_profile_email)
        val image: ImageView = findViewById(R.id.image_profilePics)
        take_picture = findViewById(R.id.image_profilePics)
        val editBtn: Button = findViewById(R.id.bt_profile_editBtn)
        editBtn.setOnClickListener(View.OnClickListener {
            //
        })
        //Progress bar
        hud= KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Uploading Image...")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        //Retrieve users update
        mUsers.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val name1 = p0.child("name").value.toString()
                val email2 = p0.child("email").value.toString()
                val image2 = p0.child("images").value.toString()
                name.text = name1
                email.text = email2
                Glide.with(this@Profile)
                    .load(image2)
                    .into(image)




            }

        })

        take_picture.setOnClickListener(View.OnClickListener {


            //create an object of the alert dialog
            val pictureDialog = AlertDialog.Builder(this)

            // we set out title
            pictureDialog.setTitle("Choose from gallery")

            //we specify the options on this line
            val pictureDialogItems = arrayOf("Select photo from gallery")
            //we set our actions here. if user select any option what should it do
            pictureDialog.setItems(
                pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    //action 1 chooses image from the gallery
                    0 -> choosePhotoFromGallary()//this function that performs the action is below
                }
            }
            //always put this line for the dialog to show
            pictureDialog.show()
        })


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    fun choosePhotoFromGallary() {

        //create an object of an Intent that picks files for you and spcify that it should pick images
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    //After selecting an image from gallery or capturing photo from camera, an onActivityResult() method is executed.
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        // checks if we picked image from Gallery
        if (requestCode == GALLERY) {
            if (data != null) {
                //gets the image we picked
                contentURI = data.data!!
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    //displays the image for us on our image view
                    picture_display = findViewById(R.id.image_profilePics)
                    picture_display.setImageBitmap(bitmap)
                    hud.show()
                    uploadImage()
                }
                //catches erros if there is any
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@Profile, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }


    }
    fun uploadImage(){
        val ref = storageReference!!.child("image/"+ UUID.randomUUID().toString())
        ref.putFile(contentURI!!)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    val downloadUrl = task.result.toString()
                    val imgref = FirebaseDatabase.getInstance().getReference("/Users/$userId")
//                    imgref.child("image").push().setValue(downloadUrl)
                    Log.i("result", "$downloadUrl")
                    hud.dismiss()
                    Toast.makeText(this,"Uploaded successful",Toast.LENGTH_LONG).show()

                }else{
                    Toast.makeText(this,"Failed upload",Toast.LENGTH_LONG).show()
                }
            }

//            .addOnSuccessListener {
//                ref.downloadUrl
//                 Log.i("name", "${ref.downloadUrl}")
////
////                val imgref = FirebaseDatabase.getInstance().getReference("/Users/$userId")
////                imgref.child("images").push().setValue(ref.downloadUrl)
//                hud.dismiss()
//
//
//                Toast.makeText(this,"Uploaded successful",Toast.LENGTH_LONG).show()
//            }.addOnFailureListener {e ->
//                hud.dismiss()
//
//                Toast.makeText(this,"Uploaded successful" + e.message,Toast.LENGTH_LONG).show()
//            }



    }
}
