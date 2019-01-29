package com.habeshastudio.mahder.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.habeshastudio.mahder.R
import com.habeshastudio.mahder.model.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DateFormat
import java.util.*

class UsersActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    var newCount: Long = -1
    private var mCurrent_user: FirebaseUser? = null

    private var mUsersList: RecyclerView? = null
    private var mUsersDatabase: DatabaseReference? = null

    private var mUserIdDatabase: DatabaseReference? = null
    internal lateinit var myDialog: Dialog

    private var mFriendReqDatabase: DatabaseReference? = null
    private var mFriendsListDatabase: DatabaseReference? = null
    private var mNotificationDatabase: DatabaseReference? = null

    private var mLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        myDialog = Dialog(this)

        mToolbar = findViewById<View>(R.id.users_appBar) as Toolbar
        setSupportActionBar(mToolbar)

        supportActionBar!!.title = "All Users"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mCurrent_user = FirebaseAuth.getInstance().currentUser
        mLayoutManager = LinearLayoutManager(this)

        mUsersDatabase = FirebaseDatabase.getInstance().reference.child("Users")

        mFriendReqDatabase = FirebaseDatabase.getInstance().reference.child("Friend_req")
        mFriendsListDatabase = FirebaseDatabase.getInstance().reference.child("Friends")
        mNotificationDatabase = FirebaseDatabase.getInstance().reference.child("Notifications")



        mUsersList = findViewById<View>(R.id.users_list) as RecyclerView
        mUsersList!!.setHasFixedSize(true)
        mUsersList!!.layoutManager = LinearLayoutManager(this)
        mCurrentState = "not_friends"


    }

    override fun onStart() {
        super.onStart()
        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users::class.java,
                R.layout.users_single_layout,
                UsersViewHolder::class.java,
                mUsersDatabase

        ) {
            override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                usersViewHolder.setName(users.name)
                usersViewHolder.setStatus(users.status)
                usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                val user_id = getRef(position).key

                usersViewHolder.mView.setOnClickListener {
                    if (user_id == mCurrent_user!!.uid) {
                        val settingsIntent = Intent(this@UsersActivity, SettingsActivity::class.java)
                        startActivity(settingsIntent)
                    } else {


                        val options = arrayOf<CharSequence>("View Profile", "Send message")

                        val builder = AlertDialog.Builder(this@UsersActivity)

                        builder.setTitle("Select Options")
                        builder.setItems(options) { dialogInterface, i ->
                            //Click Event for each item.
                            if (i == 0) {

                                val txtclose: TextView
                                myDialog.setContentView(R.layout.profile_popup)
                                txtclose = myDialog.findViewById<View>(R.id.txtclose) as TextView
                                mProfileImageView = myDialog.findViewById(R.id.user_profile_image)
                                mProfileName = myDialog.findViewById(R.id.profile_friend_name)
                                mProfileStatus = myDialog.findViewById(R.id.profile_friend_profession)
                                mFollowersCount = myDialog.findViewById(R.id.profile_followers_count)
                                mAnswersCount = myDialog.findViewById(R.id.profile_answers_count)
                                mVotesCount = myDialog.findViewById(R.id.profile_votes_count)
                                mProfileSendReqButton = myDialog.findViewById(R.id.btnfollow)
                                mLikeButton = myDialog.findViewById(R.id.like)
                                mHateButton = myDialog.findViewById(R.id.hate)

                                retriveData(user_id)

                                txtclose.setOnClickListener { myDialog.dismiss() }

                                mLikeButton!!.setOnClickListener {

                                    mUsersDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError?) {
                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                        }

                                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                                            var vote = dataSnapshot.child("votes").value!!.toString()
                                            var count: Long = vote.toLong()

                                            if (newCount < count)
                                                newCount = count + 1
                                            //mFollowersCount!!.text =vote.toString()
                                            mVotesCount!!.text = newCount.toString()
                                            mUserIdDatabase!!.child("votes").setValue(newCount.toString())
                                        }
                                    })


                                }
//                                mHateButton!!.setOnClickListener{
//                                    var newCount: Long = 1000000;
//
//                                    mUsersDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
//                                        override fun onCancelled(p0: DatabaseError?) {
//                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                                        }
//
//                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//                                            var vote = dataSnapshot.child("votes").value!!.toString()
//                                            var count : Long = vote.toLong()
//
//                                            if (newCount>count)
//                                                newCount = count-1
//                                            //mFollowersCount!!.text =vote.toString()
//                                            mVotesCount!!.text =newCount.toString()
//                                            mUserIdDatabase!!.child("votes").setValue(newCount.toString())
//                                        }
//                                    })
//
//
//                                }

                                mProfileSendReqButton!!.setOnClickListener { onFollowPressed(user_id) }

                                myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                myDialog.show()
                                //                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                //                        profileIntent.putExtra("user_id", user_id);
                                //                        startActivity(profileIntent);

                            }

                            if (i == 1) {

                                val chatIntent = Intent(this@UsersActivity, ChatActivity::class.java)
                                chatIntent.putExtra("user_id", user_id)





                                mUsersDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError?) {
                                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                        val userName = dataSnapshot.child("name").value!!.toString()
                                        chatIntent.putExtra("user_name", userName)
                                        startActivity(chatIntent)
                                    }
                                })
                            }
                        }

                        builder.show()


                    }
                }

            }
        }
        mUsersList!!.adapter = firebaseRecyclerAdapter
    }

    private fun onFollowPressed(user_id: String) {

        mProfileSendReqButton!!.isEnabled = false

        // - ----------------- Friendoch Kalhonu ---------------------

        if (mCurrentState == "not_friends") {
            mFriendReqDatabase!!.child(mCurrent_user!!.uid).child(user_id).child("request_type")
                    .setValue("sent").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendReqDatabase!!.child(user_id).child(mCurrent_user!!.uid).child("request_type").setValue("received").addOnSuccessListener {
                        Toast.makeText(this@UsersActivity, "Friend Request Sent", Toast.LENGTH_SHORT).show()

                        val notificationData = HashMap<String, String>()
                        notificationData["from"] = mCurrent_user!!.uid
                        notificationData["type"] = "request"

                        mNotificationDatabase!!.child(user_id).push().setValue(notificationData).addOnSuccessListener {
                            mCurrentState = "req_sent"
                            mProfileSendReqButton!!.text = "Cancel Friend Request"
                        }
                    }
                } else {
                    Toast.makeText(this@UsersActivity, "Failed Sending Request", Toast.LENGTH_SHORT).show()
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
                }
            }
        }
        //----------request ketekebeln---------------//
        if (mCurrentState == "req_received") {

            val currentDate = DateFormat.getDateTimeInstance().format(Date())

            mFriendsListDatabase!!.child(mCurrent_user!!.uid).child(user_id).child("date").setValue(currentDate)
                    .addOnSuccessListener {
                        mFriendsListDatabase!!.child(user_id).child(mCurrent_user!!.uid).child("date").setValue(currentDate)
                                .addOnSuccessListener {
                                    mFriendReqDatabase!!.child(mCurrent_user!!.uid).child(user_id).removeValue().addOnSuccessListener {
                                        mFriendReqDatabase!!.child(user_id).child(mCurrent_user!!.uid).removeValue().addOnSuccessListener {
                                            mProfileSendReqButton!!.isEnabled = true
                                            mCurrentState = "friends"
                                            mProfileSendReqButton!!.text = "Unfriend"
                                        }
                                    }
                                }
                    }

        }


    }

    private fun retriveData(user_id: String) {

        mUserIdDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(user_id)
        mFriendReqDatabase = FirebaseDatabase.getInstance().reference.child("Friend_req")
        mFriendsListDatabase = FirebaseDatabase.getInstance().reference.child("Friends")
        mNotificationDatabase = FirebaseDatabase.getInstance().reference.child("Notifications")

        mUserIdDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val display_name = dataSnapshot.child("name").value!!.toString()
                val status = dataSnapshot.child("status").value!!.toString()
                val image = dataSnapshot.child("image").value!!.toString()
                val votes = dataSnapshot.child("votes").value!!.toString()
                val answers = dataSnapshot.child("answers").value!!.toString()
                val followers = dataSnapshot.child("followers").value!!.toString()
                val city = dataSnapshot.child("city").value!!.toString()

                mAnswersCount!!.text = answers
                mVotesCount!!.text = votes
                mFollowersCount!!.text = followers

                mProfileName!!.text = display_name
                mProfileStatus!!.text = "$status, $city"

                Picasso.with(this@UsersActivity).load(image).placeholder(R.drawable.user).into(mProfileImageView)

                //---------------Friends List / Request Feature----------
                mFriendReqDatabase!!.child(mCurrent_user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            val req_type = dataSnapshot.child(user_id).child("request_type").value!!.toString()

                            if (req_type == "received") {

                                mCurrentState = "req_received"
                                mProfileSendReqButton!!.text = "Accept Friend Request"
                            } else if (req_type == "sent") {

                                mCurrentState = "req_sent"
                                mProfileSendReqButton!!.text = "Cancel Friend Request"


                            }
                        } else {

                            mFriendsListDatabase!!.child(mCurrent_user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrentState = "friends"
                                        mProfileSendReqButton!!.text = "Unfriend"
                                    }


                                }

                                override fun onCancelled(databaseError: DatabaseError) {


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
    }

    class UsersViewHolder(internal var mView: View) : RecyclerView.ViewHolder(mView) {

        fun setName(name: String) {
            val userNameView = mView.findViewById<View>(R.id.user_single_name) as TextView
            userNameView.text = name
        }

        fun setStatus(status: String) {
            val userStatusView = mView.findViewById<View>(R.id.user_single_status) as TextView
            userStatusView.text = status
        }

        fun setUserImage(userThumbImage: String, ctx: Context) {
            val user_image = mView.findViewById<View>(R.id.user_single_image) as CircleImageView
            Picasso.with(ctx).load(userThumbImage).placeholder(R.drawable.user).into(user_image)
        }
    }

    companion object {

        private var mProfileImageView: ImageView? = null
        private var mProfileName: TextView? = null
        private var mProfileStatus: TextView? = null
        private var mFollowersCount: TextView? = null
        private var mAnswersCount: TextView? = null
        private var mVotesCount: TextView? = null
        private var mProfileSendReqButton: Button? = null
        private var mLikeButton: ImageView? = null
        private var mHateButton: ImageView? = null
        private var mCurrentState: String? = null
    }
}
