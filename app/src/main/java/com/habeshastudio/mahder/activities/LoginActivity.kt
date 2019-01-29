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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.habeshastudio.mahder.R

class LoginActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null

    private var mLoginEmail: TextInputLayout? = null
    private var mLoginPassword: TextInputLayout? = null
    private var mLoginButton: Button? = null
    private var mLoginProgress: ProgressDialog? = null

    private var mAuth: FirebaseAuth? = null
    private var mCurrentUser: FirebaseUser? = null
    private var mUserDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        mLoginEmail = findViewById<View>(R.id.login_email) as TextInputLayout
        mLoginPassword = findViewById<View>(R.id.login_password) as TextInputLayout
        mLoginButton = findViewById<View>(R.id.login_btn) as Button

        mLoginProgress = ProgressDialog(this)

        mLoginButton!!.setOnClickListener {
            val email = mLoginEmail!!.editText!!.text.toString()
            val password = mLoginPassword!!.editText!!.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {


                mLoginProgress!!.setTitle("Logging in ...")
                mLoginProgress!!.setMessage("Please wait while we Log you in")
                mLoginProgress!!.setCanceledOnTouchOutside(false)
                mLoginProgress!!.show()
                loginUser(email, password)
            }
        }

        //Toolbar
        mToolbar = findViewById<View>(R.id.login_toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Create Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun loginUser(email: String, password: String) {

        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        mCurrentUser = FirebaseAuth.getInstance().currentUser
                        val currentUid = mCurrentUser!!.uid
                        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users")

                        mUserDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.child(currentUid).child("name").exists()) {

                                    mLoginProgress!!.dismiss()
                                    val mainIntent = Intent(this@LoginActivity, Dashboard::class.java)
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(mainIntent)
                                    finish()
                                } else {
                                    val formIntent = Intent(this@LoginActivity, FormActivity::class.java)
                                    startActivity(formIntent)
                                }
                            }
                        })
                    } else {
                        // If sign in fails, display a message to the user.
                        mLoginProgress!!.hide()
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }

    }
}
