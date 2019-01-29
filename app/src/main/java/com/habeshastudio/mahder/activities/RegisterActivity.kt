package com.habeshastudio.mahder.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.habeshastudio.mahder.R
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var mDisplayName: TextInputLayout? = null
    private var mEmail: TextInputLayout? = null
    private var mPassword: TextInputLayout? = null
    private var mCreateButton: Button? = null

    //firebase auth
    private var mAuth: FirebaseAuth? = null
    private var mToolbar: Toolbar? = null
    private var mDatabase: DatabaseReference? = null

    //progress dialog
    private var mRegProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mDisplayName = findViewById<View>(R.id.reg_display_name) as TextInputLayout
        mEmail = findViewById<View>(R.id.login_email) as TextInputLayout
        mPassword = findViewById<View>(R.id.login_password) as TextInputLayout
        mCreateButton = findViewById<View>(R.id.reg_create_btn) as Button

        //Toolbar set
        mToolbar = findViewById<View>(R.id.register_toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Welcome"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //progress
        mRegProgress = ProgressDialog(this)

        //declare firebase auth
        mAuth = FirebaseAuth.getInstance()

        mCreateButton!!.setOnClickListener {
            val display_name = mDisplayName!!.editText!!.text.toString()
            val email = mEmail!!.editText!!.text.toString()
            val password = mPassword!!.editText!!.text.toString()

            if (!TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {


                mRegProgress!!.setTitle("Registering ...")
                mRegProgress!!.setMessage("Please wait while we create your account")
                mRegProgress!!.setCanceledOnTouchOutside(false)
                mRegProgress!!.show()
                register_user(display_name, email, password)

            }
        }
    }

    private fun register_user(display_name: String, email: String, password: String) {

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val uId = currentUser!!.uid

                        mDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(uId)

                        val userMap = HashMap<String, String>()
                        userMap["name"] = display_name
                        userMap["status"] = "Unknown Profession"
                        userMap["votes"] = "0"
                        userMap["answers"] = "0"
                        userMap["followers"] = "0"
                        userMap["city"] = "Unknown"
                        userMap["image"] = "default"
                        userMap["thumb_image"] = "default"
                        mDatabase!!.setValue(userMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                mRegProgress!!.dismiss()
                                val mainIntent = Intent(this@RegisterActivity, Dashboard::class.java)
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(mainIntent)
                                finish()
                            }
                        }

                    } else {

                        mRegProgress!!.hide()
                        Toast.makeText(this@RegisterActivity, "Something went wrong, \n Please try again later!.",
                                Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }

    }

    /*Checking whether there is a network connection or not

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    */
}
