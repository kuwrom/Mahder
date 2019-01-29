package com.habeshastudio.mahder.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.habeshastudio.mahder.MainActivity
import com.habeshastudio.mahder.R
import java.util.concurrent.TimeUnit

class PhoneAuth : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private val mLoginProgress: ProgressDialog? = null


    private var phoneText: EditText? = null
    private var codeText: EditText? = null
    private var verifyButton: Button? = null
    private var sendButton: Button? = null
    private var statusText: TextView? = null
    private val isNew = false

    //firebase
    private var mCurrentUser: FirebaseUser? = null
    private var mUserDatabase: DatabaseReference? = null

    internal var isSent = false
    private var phoneVerificationId: String? = null
    private var verificationCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private var fbAuth: FirebaseAuth? = null
    private val mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)


        phoneText = findViewById<View>(R.id.phoneText) as EditText
        codeText = findViewById<View>(R.id.codeText) as EditText
        verifyButton = findViewById<View>(R.id.verifyButton) as Button
        sendButton = findViewById<View>(R.id.sendButton) as Button
        statusText = findViewById<View>(R.id.statusText) as TextView


        //Toolbar
        mToolbar = findViewById<View>(R.id.phone_appbar) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Stgeref Tawerawaleh"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        verifyButton!!.isEnabled = false
        statusText!!.text = "Signed Out"

        fbAuth = FirebaseAuth.getInstance()

    }

    fun sendCode(view: View) {

        val phoneNumber = phoneText!!.text.toString()

        setUpVerificatonCallbacks()

        if (isSent) {

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    verificationCallbacks!!,
                    resendToken)
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                verificationCallbacks!!)
    }

    private fun setUpVerificatonCallbacks() {

        verificationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(
                    credential: PhoneAuthCredential) {
                verifyButton!!.isEnabled = false
                codeText!!.setText("")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d(TAG, "Invalid credential: " + e.getLocalizedMessage())
                } else if (e is FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                    Log.d(TAG, "SMS Quota exceeded.")
                }
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken?) {

                phoneVerificationId = verificationId
                resendToken = token

                verifyButton!!.isEnabled = true
                sendButton!!.text = "Resend"
                isSent = true
            }
        }
    }

    fun verifyCode(view: View) {

        //        mLoginProgress.setTitle("Logging in ...");
        //        mLoginProgress.setMessage("Please wait while we Log you in");
        //        mLoginProgress.setCanceledOnTouchOutside(false);
        //        mLoginProgress.show();

        val code = codeText!!.text.toString()

        val credential = PhoneAuthProvider.getCredential(phoneVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        fbAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        mCurrentUser = FirebaseAuth.getInstance().currentUser
                        val currentUid = mCurrentUser!!.uid
                        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users")

                        mUserDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.child(currentUid).child("name").exists()) {
                                    val mainIntent = Intent(this@PhoneAuth, MainActivity::class.java)
                                    startActivity(mainIntent)
                                } else {
                                    val formIntent = Intent(this@PhoneAuth, FormActivity::class.java)
                                    startActivity(formIntent)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })

                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
    }

    companion object {

        private val TAG = "PhoneAuth"
    }

}