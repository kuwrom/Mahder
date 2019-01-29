package com.habeshastudio.mahder.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.habeshastudio.mahder.R

class StatusActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var mStatus: TextInputLayout? = null
    private var mSubmitButton: Button? = null

    //Firebase
    private var mStatusDatabase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null

    //Progress dialog
    private var mProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        val current_uId = mCurrentUser!!.uid
        mStatusDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(current_uId)

        mToolbar = findViewById<View>(R.id.status_appbar) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Account Profession"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Progress dialog


        val status_value = intent.getStringExtra("status_value")
        mStatus = findViewById<View>(R.id.status_input) as TextInputLayout
        mStatus!!.editText!!.setText(status_value)
        mSubmitButton = findViewById<View>(R.id.status_submit_btn) as Button

        mSubmitButton!!.setOnClickListener {
            //Progress
            mProgress = ProgressDialog(this@StatusActivity)
            mProgress!!.setTitle("Updating professoin ...")
            mProgress!!.setMessage("Please wait while we update your Profession")
            //mProgress.setCanceledOnTouchOutside(false);
            mProgress!!.show()

            val status = mStatus!!.editText!!.text.toString()
            mStatusDatabase!!.child("status").setValue(status).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mProgress!!.dismiss()
                } else {
                    Toast.makeText(applicationContext, "There was some error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
