package com.habeshastudio.mahder.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.habeshastudio.mahder.R
import com.habeshastudio.mahder.model.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class Professionals : AppCompatActivity() {
    private var mToolbar: Toolbar? = null

    private var mUsersList: RecyclerView? = null

    private var mDoctorsDatabase: DatabaseReference? = null
    private var mEngineersDatabase: DatabaseReference? = null
    private var mLawyersDatabase: DatabaseReference? = null
    private var mTechniciansDatabase: DatabaseReference? = null
    private var mTeachersDatabase: DatabaseReference? = null
    private var mMerchantsDatabase: DatabaseReference? = null
    private var mDriversDatabase: DatabaseReference? = null
    private var mArtistsDatabase: DatabaseReference? = null
    private var mCurrent_user: FirebaseUser? = null
    internal lateinit var myDialog: Dialog


    private var mLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professionals)

        mToolbar = findViewById<View>(R.id.professionals_appBar) as Toolbar
        setSupportActionBar(mToolbar)

        myDialog = Dialog(this)


        supportActionBar!!.title = intent.getStringExtra("profession") + "s"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDoctorsDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Doctors")
        mEngineersDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Engineers")
        mLawyersDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Lawyers")
        mTechniciansDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Technicians")
        mTeachersDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Teachers")
        mMerchantsDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Merchants")
        mDriversDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Drivers")
        mArtistsDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Artists")
        mCurrent_user = FirebaseAuth.getInstance().currentUser

        mLayoutManager = LinearLayoutManager(this)

        mUsersList = findViewById<View>(R.id.professionals_list) as RecyclerView
        mUsersList!!.setHasFixedSize(true)
        mUsersList!!.layoutManager = mLayoutManager


    }

    override fun onStart() {
        super.onStart()

        val profession = intent.getStringExtra("profession")

        if (profession == "Doctor") {

            val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                    Users::class.java,
                    R.layout.users_single_layout,
                    UsersViewHolder::class.java,
                    mDoctorsDatabase

            ) {
                override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                    usersViewHolder.setDisplayName(users.name)
                    usersViewHolder.setUserStatus(users.status)
                    usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                    val user_id = getRef(position).key

                    usersViewHolder.mView.setOnClickListener {


                        if (user_id == mCurrent_user!!.uid) {
                            val settingsIntent = Intent(this@Professionals, SettingsActivity::class.java)
                            startActivity(settingsIntent)
                        } else {

                            val options = arrayOf<CharSequence>("View Profile", "Send message")

                            val builder = android.app.AlertDialog.Builder(this@Professionals)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { dialogInterface, i ->
                                //Click Event for each item.
                                if (i == 0) {

                                    val profileIntent = Intent(this@Professionals, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", user_id)
                                    startActivity(profileIntent)
                                }

                                if (i == 1) {

                                    val chatIntent = Intent(this@Professionals, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", user_id)


                                    mDoctorsDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
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

        } else if (profession == "Engineer") {
            val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                    Users::class.java,
                    R.layout.users_single_layout,
                    UsersViewHolder::class.java,
                    mEngineersDatabase

            ) {
                override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                    usersViewHolder.setDisplayName(users.name)

                    usersViewHolder.setUserStatus(users.status)

                    usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                    val user_id = getRef(position).key

                    usersViewHolder.mView.setOnClickListener {


                        if (user_id == mCurrent_user!!.uid) {
                            val settingsIntent = Intent(this@Professionals, SettingsActivity::class.java)
                            startActivity(settingsIntent)
                        } else {

                            val options = arrayOf<CharSequence>("View Profile", "Send message")

                            val builder = android.app.AlertDialog.Builder(this@Professionals)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { dialogInterface, i ->
                                //Click Event for each item.
                                if (i == 0) {

                                    val profileIntent = Intent(this@Professionals, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", user_id)
                                    startActivity(profileIntent)
                                }

                                if (i == 1) {

                                    val chatIntent = Intent(this@Professionals, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", user_id)


                                    mEngineersDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
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
        } else if (profession == "Lawyer") {

            val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                    Users::class.java,
                    R.layout.users_single_layout,
                    UsersViewHolder::class.java,
                    mLawyersDatabase

            ) {
                override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                    usersViewHolder.setDisplayName(users.name)
                    usersViewHolder.setUserStatus(users.status)
                    usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                    val user_id = getRef(position).key

                    usersViewHolder.mView.setOnClickListener {


                        if (user_id == mCurrent_user!!.uid) {
                            val settingsIntent = Intent(this@Professionals, SettingsActivity::class.java)
                            startActivity(settingsIntent)
                        } else {

                            val options = arrayOf<CharSequence>("View Profile", "Send message")

                            val builder = android.app.AlertDialog.Builder(this@Professionals)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { dialogInterface, i ->
                                //Click Event for each item.
                                if (i == 0) {

                                    val profileIntent = Intent(this@Professionals, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", user_id)
                                    startActivity(profileIntent)
                                }

                                if (i == 1) {

                                    val chatIntent = Intent(this@Professionals, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", user_id)


                                    mLawyersDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
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

        } else if (profession == "Technician") {

            val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                    Users::class.java,
                    R.layout.users_single_layout,
                    UsersViewHolder::class.java,
                    mTechniciansDatabase

            ) {
                override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                    usersViewHolder.setDisplayName(users.name)
                    usersViewHolder.setUserStatus(users.status)
                    usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                    val user_id = getRef(position).key

                    usersViewHolder.mView.setOnClickListener {


                        if (user_id == mCurrent_user!!.uid) {
                            val settingsIntent = Intent(this@Professionals, SettingsActivity::class.java)
                            startActivity(settingsIntent)
                        } else {

                            val options = arrayOf<CharSequence>("View Profile", "Send message")

                            val builder = android.app.AlertDialog.Builder(this@Professionals)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { dialogInterface, i ->
                                //Click Event for each item.
                                if (i == 0) {

                                    val profileIntent = Intent(this@Professionals, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", user_id)
                                    startActivity(profileIntent)
                                }

                                if (i == 1) {

                                    val chatIntent = Intent(this@Professionals, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", user_id)


                                    mTechniciansDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
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

        } else if (profession == "Teacher") {

            val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                    Users::class.java,
                    R.layout.users_single_layout,
                    UsersViewHolder::class.java,
                    mTeachersDatabase

            ) {
                override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                    usersViewHolder.setDisplayName(users.name)
                    usersViewHolder.setUserStatus(users.status)
                    usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                    val user_id = getRef(position).key

                    usersViewHolder.mView.setOnClickListener {


                        if (user_id == mCurrent_user!!.uid) {
                            val settingsIntent = Intent(this@Professionals, SettingsActivity::class.java)
                            startActivity(settingsIntent)
                        } else {

                            val options = arrayOf<CharSequence>("View Profile", "Send message")

                            val builder = android.app.AlertDialog.Builder(this@Professionals)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { dialogInterface, i ->
                                //Click Event for each item.
                                if (i == 0) {

                                    val profileIntent = Intent(this@Professionals, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", user_id)
                                    startActivity(profileIntent)
                                }

                                if (i == 1) {

                                    val chatIntent = Intent(this@Professionals, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", user_id)


                                    mTeachersDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
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

        } else if (profession == "Merchant") {

            val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                    Users::class.java,
                    R.layout.users_single_layout,
                    UsersViewHolder::class.java,
                    mMerchantsDatabase

            ) {
                override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                    usersViewHolder.setDisplayName(users.name)
                    usersViewHolder.setUserStatus(users.status)
                    usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                    val user_id = getRef(position).key

                    usersViewHolder.mView.setOnClickListener {


                        if (user_id == mCurrent_user!!.uid) {
                            val settingsIntent = Intent(this@Professionals, SettingsActivity::class.java)
                            startActivity(settingsIntent)
                        } else {

                            val options = arrayOf<CharSequence>("View Profile", "Send message")

                            val builder = android.app.AlertDialog.Builder(this@Professionals)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { dialogInterface, i ->
                                //Click Event for each item.
                                if (i == 0) {

                                    val profileIntent = Intent(this@Professionals, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", user_id)
                                    startActivity(profileIntent)
                                }

                                if (i == 1) {

                                    val chatIntent = Intent(this@Professionals, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", user_id)


                                    mMerchantsDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
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

        } else if (profession == "Driver") {

            val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                    Users::class.java,
                    R.layout.users_single_layout,
                    UsersViewHolder::class.java,
                    mDriversDatabase

            ) {
                override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                    usersViewHolder.setDisplayName(users.name)
                    usersViewHolder.setUserStatus(users.status)
                    usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                    val user_id = getRef(position).key

                    usersViewHolder.mView.setOnClickListener {


                        if (user_id == mCurrent_user!!.uid) {
                            val settingsIntent = Intent(this@Professionals, SettingsActivity::class.java)
                            startActivity(settingsIntent)
                        } else {

                            val options = arrayOf<CharSequence>("View Profile", "Send message")

                            val builder = android.app.AlertDialog.Builder(this@Professionals)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { dialogInterface, i ->
                                //Click Event for each item.
                                if (i == 0) {

                                    val profileIntent = Intent(this@Professionals, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", user_id)
                                    startActivity(profileIntent)
                                }

                                if (i == 1) {

                                    val chatIntent = Intent(this@Professionals, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", user_id)


                                    mDriversDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
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

        } else if (profession == "Artist") {

            val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                    Users::class.java,
                    R.layout.users_single_layout,
                    UsersViewHolder::class.java,
                    mArtistsDatabase

            ) {
                override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                    usersViewHolder.setDisplayName(users.name)
                    usersViewHolder.setUserStatus(users.status)
                    usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                    val user_id = getRef(position).key

                    usersViewHolder.mView.setOnClickListener {


                        if (user_id == mCurrent_user!!.uid) {
                            val settingsIntent = Intent(this@Professionals, SettingsActivity::class.java)
                            startActivity(settingsIntent)
                        } else {

                            val options = arrayOf<CharSequence>("View Profile", "Send message")

                            val builder = android.app.AlertDialog.Builder(this@Professionals)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { dialogInterface, i ->
                                //Click Event for each item.
                                if (i == 0) {

                                    val profileIntent = Intent(this@Professionals, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", user_id)
                                    startActivity(profileIntent)
                                }

                                if (i == 1) {

                                    val chatIntent = Intent(this@Professionals, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", user_id)


                                    mArtistsDatabase!!.child(user_id).addValueEventListener(object : ValueEventListener {
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

    }


    class UsersViewHolder(internal var mView: View) : RecyclerView.ViewHolder(mView) {

        fun setDisplayName(name: String) {

            val userNameView = mView.findViewById<View>(R.id.user_single_name) as TextView
            userNameView.text = name

        }

        fun setUserStatus(status: String) {

            val userStatusView = mView.findViewById<View>(R.id.user_single_status) as TextView
            userStatusView.text = status


        }

        fun setUserImage(thumb_image: String, ctx: Context) {

            val userImageView = mView.findViewById<View>(R.id.user_single_image) as CircleImageView

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.user).into(userImageView)

        }


    }

}
