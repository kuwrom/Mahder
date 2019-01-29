package com.habeshastudio.mahder.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.habeshastudio.mahder.R
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private var mProfileImageView: ImageView? = null
    private var mProfileName: TextView? = null
    private var mProfileStatus: TextView? = null
    private var mFollowersCount: TextView? = null
    private val mAnswersCount: TextView? = null
    private val mVotesCount: TextView? = null
    private var mProfileSendReqButton: Button? = null
    private var mProfileDeclineBtn: Button? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mCurrentState: String? = null

    private var mCurrent_user: FirebaseUser? = null

    private var mUsersDatabase: DatabaseReference? = null
    private var mFriendReqDatabase: DatabaseReference? = null
    private var mFriendsListDatabase: DatabaseReference? = null
    private var mNotificationDatabase: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user_id = intent.getStringExtra("user_id")

        mUsersDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(user_id)
        mFriendReqDatabase = FirebaseDatabase.getInstance().reference.child("Friend_req")
        mFriendsListDatabase = FirebaseDatabase.getInstance().reference.child("Friends")
        mNotificationDatabase = FirebaseDatabase.getInstance().reference.child("Notifications")

        mCurrent_user = FirebaseAuth.getInstance().currentUser

        mProfileImageView = findViewById(R.id.friend_profile_picture)
        mProfileName = findViewById(R.id.profile_display_name)
        mProfileStatus = findViewById(R.id.profile_status)
        mFollowersCount = findViewById(R.id.profile_total_friends)
        mProfileSendReqButton = findViewById(R.id.profile_friend_btn)
        mProfileDeclineBtn = findViewById(R.id.profile_decline_btn)


        mProfileDeclineBtn!!.visibility = View.INVISIBLE
        mProfileDeclineBtn!!.isEnabled = false

        mCurrentState = "not_friends"



        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setTitle("Loading User data")
        mProgressDialog!!.setMessage("please wait while we load the user data")
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.show()

        mUsersDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val display_name = dataSnapshot.child("name").value!!.toString()
                val status = dataSnapshot.child("status").value!!.toString()
                val image = dataSnapshot.child("image").value!!.toString()

                mProfileName!!.text = display_name
                mProfileStatus!!.text = status

                Picasso.with(this@ProfileActivity).load(image).placeholder(R.drawable.user).into(mProfileImageView)

                //---------------Friends List / Request Feature----------
                mFriendReqDatabase!!.child(mCurrent_user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            val req_type = dataSnapshot.child(user_id).child("request_type").value!!.toString()

                            if (req_type == "received") {

                                mCurrentState = "req_received"
                                mProfileSendReqButton!!.text = "Accept Friend Request"

                                mProfileDeclineBtn!!.visibility = View.VISIBLE
                                mProfileDeclineBtn!!.isEnabled = true

                            } else if (req_type == "sent") {

                                mCurrentState = "req_sent"
                                mProfileSendReqButton!!.text = "Cancel Friend Request"

                                mProfileDeclineBtn!!.visibility = View.INVISIBLE
                                mProfileDeclineBtn!!.isEnabled = false

                            }
                            mProgressDialog!!.dismiss()
                        } else {

                            mFriendsListDatabase!!.child(mCurrent_user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrentState = "friends"
                                        mProfileSendReqButton!!.text = "Unfriend"

                                        mProfileDeclineBtn!!.visibility = View.INVISIBLE
                                        mProfileDeclineBtn!!.isEnabled = false
                                    }

                                    mProgressDialog!!.dismiss()

                                }

                                override fun onCancelled(databaseError: DatabaseError) {

                                    mProgressDialog!!.dismiss()

                                }
                            })

                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        //cancel button

        mProfileDeclineBtn!!.setOnClickListener {
            mFriendReqDatabase!!.child(mCurrent_user!!.uid).child(user_id).removeValue().addOnSuccessListener {
                mFriendReqDatabase!!.child(user_id).child(mCurrent_user!!.uid).removeValue().addOnSuccessListener {
                    mProfileSendReqButton!!.isEnabled = true
                    mCurrentState = "not_friends"
                    mProfileSendReqButton!!.text = "Add Friend"

                    mProfileDeclineBtn!!.visibility = View.INVISIBLE
                    mProfileDeclineBtn!!.isEnabled = false
                }
            }
        }

        //accept button

        mProfileSendReqButton!!.setOnClickListener {
            mProfileSendReqButton!!.isEnabled = false

            // - ----------------- Friendoch Kalhonu ---------------------

            if (mCurrentState == "not_friends") {
                mFriendReqDatabase!!.child(mCurrent_user!!.uid).child(user_id).child("request_type")
                        .setValue("sent").addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mFriendReqDatabase!!.child(user_id).child(mCurrent_user!!.uid).child("request_type").setValue("received").addOnSuccessListener {
                            Toast.makeText(this@ProfileActivity, "Friend Request Sent", Toast.LENGTH_SHORT).show()

                            val notificationData = HashMap<String, String>()
                            notificationData["from"] = mCurrent_user!!.uid
                            notificationData["type"] = "request"

                            mNotificationDatabase!!.child(user_id).push().setValue(notificationData).addOnSuccessListener {
                                mCurrentState = "req_sent"
                                mProfileSendReqButton!!.text = "Cancel Friend Request"

                                mProfileDeclineBtn!!.visibility = View.INVISIBLE
                                mProfileDeclineBtn!!.isEnabled = false
                            }
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Failed Sending Request", Toast.LENGTH_SHORT).show()
                    }
                    mProfileSendReqButton!!.isEnabled = true
                }
            }
            // - ----------------- Request ketelake ---------------------
            if (mCurrentState == "req_sent") {

                mFriendReqDatabase!!.child(mCurrent_user!!.uid).child(user_id).removeValue().addOnSuccessListener {
                    mFriendReqDatabase!!.child(user_id).child(mCurrent_user!!.uid).removeValue().addOnSuccessListener {
                        mProfileSendReqButton!!.isEnabled = true
                        mCurrentState = "not_friends"
                        mProfileSendReqButton!!.text = "Add Friend"

                        mProfileDeclineBtn!!.visibility = View.INVISIBLE
                        mProfileDeclineBtn!!.isEnabled = false
                    }
                }

            }

            //to Unfriend

            if (mCurrentState == "friends") {

                mFriendsListDatabase!!.child(mCurrent_user!!.uid).child(user_id).removeValue().addOnSuccessListener {
                    mFriendsListDatabase!!.child(user_id).child(mCurrent_user!!.uid).removeValue().addOnSuccessListener {
                        mProfileSendReqButton!!.isEnabled = true
                        mCurrentState = "not_friends"
                        mProfileSendReqButton!!.text = "Add Friend"

                        mProfileDeclineBtn!!.visibility = View.INVISIBLE
                        mProfileDeclineBtn!!.isEnabled = false
                    }
                }
            }
            //----------request received---------------//
            if (mCurrentState == "req_received") {

                val currentDate = DateFormat.getDateTimeInstance().format(Date())

                mFriendsListDatabase!!.child(mCurrent_user!!.uid).child(user_id).setValue(currentDate)
                        .addOnSuccessListener {
                            mFriendsListDatabase!!.child(user_id).child(mCurrent_user!!.uid).setValue(currentDate)
                                    .addOnSuccessListener {
                                        mFriendReqDatabase!!.child(mCurrent_user!!.uid).child(user_id).removeValue().addOnSuccessListener {
                                            mFriendReqDatabase!!.child(user_id).child(mCurrent_user!!.uid).removeValue().addOnSuccessListener {
                                                mProfileSendReqButton!!.isEnabled = true
                                                mCurrentState = "friends"
                                                mProfileSendReqButton!!.text = "Unfriend"

                                                mProfileDeclineBtn!!.visibility = View.INVISIBLE
                                                mProfileDeclineBtn!!.isEnabled = false
                                            }
                                        }
                                    }
                        }

            }
        }

    }
}
