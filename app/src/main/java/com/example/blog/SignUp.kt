package com.example.blog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kaopiz.kprogresshud.KProgressHUD

private lateinit var name:String
private lateinit var email:String
private lateinit var password:String
private lateinit var cpassword:String
private var mAuth: FirebaseAuth? = null
private var mDatabase: FirebaseDatabase? = null
private lateinit var hud: KProgressHUD
class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val login = findViewById<TextView>(R.id.tv_signUp_login)

        login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        })

        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance()

        val mUsers: DatabaseReference =mDatabase!!.reference!!.child("Users")

        val et_name = findViewById<EditText>(R.id.et_signUp_name)
        val et_email = findViewById<EditText>(R.id.et_signUp_email)
        val et_password = findViewById<EditText>(R.id.et_signUp_password)
        val et_cpassword = findViewById<EditText>(R.id.et_signUp_cpassword)
        val bt_register = findViewById<Button>(R.id.bt_signUp_register)

        hud= KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Creating User")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        bt_register.setOnClickListener(View.OnClickListener {
            name = et_name.text.toString().trim()
            email = et_email.text.toString().trim()
            password = et_password.text.toString().trim()
            cpassword = et_cpassword.text.toString().trim()

            if (name.isEmpty()){
                et_name.setError("Name field is empty!")
            }else if (email.isEmpty()|| !email.contains(".")|| !email.contains("@")){
                et_email.setError("Incorrect Email/Empty Email")
            }else if (password.isEmpty()){
                et_password.setError("Password field empty!")
            }else if (cpassword.isEmpty()){
                et_cpassword.setError("Confirm password field empty!")
            }else if (password != cpassword){
                Toast.makeText(this,"Password does not match",Toast.LENGTH_LONG).show()
            }else{

                hud.show()
                mAuth!!.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task ->
                        hud.dismiss()
                        if (task.isSuccessful){
                            //fetch the user id of the current User
                            val userId = mAuth!!.currentUser!!.uid

                            //update user profile information
                            val currentUserDb = mUsers!!.child(userId)

                            val users: UserModel = UserModel(uid = userId,name = name,email = email)

                            currentUserDb.setValue(users)

//                            currentUserDb.child("names").setValue(names)
//                            currentUserDb.child("phone").setValue(phone)
//                            currentUserDb.child("email").setValue(email)
//                            currentUserDb.child("uid").setValue(userId)

                            updateUserInfoAndUI()
                        }

                    }
            }

        })


    }
    private fun updateUserInfoAndUI() {
        //start next activity
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
