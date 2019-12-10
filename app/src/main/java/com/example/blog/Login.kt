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
import com.kaopiz.kprogresshud.KProgressHUD

private lateinit var email:String
private lateinit var password:String
private var mAuth: FirebaseAuth? = null

private lateinit var hud: KProgressHUD

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val register = findViewById<TextView>(R.id.tv_login_register)
        val et_email = findViewById<EditText>(R.id.et_login_email)
        val et_password = findViewById<EditText>(R.id.et_login_password)
        val bt_login = findViewById<Button>(R.id.bt_login_login)


        //Redirect non register users
        register.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        })
        //Get the instance of user unthentication
        mAuth = FirebaseAuth.getInstance()

//        val userId = mAuth!!.currentUser!!.uid
//
//        if (userId != null){
//            updateUi()
//
//        }
        hud= KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Authenticating User")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        bt_login.setOnClickListener(View.OnClickListener {

            email = et_email.text.toString()
            password = et_password.text.toString()

            if (email.isEmpty() || !email.contains(".") || !email.contains("@")){
                et_email.setError("Email field empty")
            }else if(password.isEmpty()){
                et_password.setError("Password field empty")
            }else{
                hud.show()
                mAuth!!.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task ->
                        hud.dismiss()
                        if (task.isSuccessful){

                            updateUi()
                        }else{
                            Toast.makeText(this,"User authentication Failed",Toast.LENGTH_LONG).show()
                        }


                    }
            }

        })

    }
    fun updateUi(){
        val toMain = Intent(this,MainActivity::class.java)
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(toMain)
        finish()
    }
}
