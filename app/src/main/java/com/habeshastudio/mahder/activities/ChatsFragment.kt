package com.habeshastudio.mahder.activities


import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.habeshastudio.mahder.R
import com.habeshastudio.mahder.model.Conv
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {


    private var mConvList: RecyclerView? = null

    private var mConvDatabase: DatabaseReference? = null
    private var mMessageDatabase: DatabaseReference? = null
    private var mUsersDatabase: DatabaseReference? = null

    private var mAuth: FirebaseAuth? = null

    private var mCurrentUserIid: String? = null

    private var mMainView: View? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mMainView = inflater!!.inflate(R.layout.fragment_chats, container, false)

        mConvList = mMainView!!.findViewById<View>(R.id.conv_list) as RecyclerView
        mAuth = FirebaseAuth.getInstance()

        mCurrentUserIid = mAuth!!.currentUser!!.uid

        mConvDatabase = FirebaseDatabase.getInstance().reference.child("Chat").child(mCurrentUserIid!!)

        mConvDatabase!!.keepSynced(true)
        mUsersDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        mMessageDatabase = FirebaseDatabase.getInstance().reference.child("messages").child(mCurrentUserIid!!)
        mUsersDatabase!!.keepSynced(true)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        mConvList!!.setHasFixedSize(true)
        mConvList!!.layoutManager = linearLayoutManager


        // Inflate the layout for this fragment
        return mMainView
    }


    override fun onStart() {
        super.onStart()

        val conversationQuery = mConvDatabase!!.orderByChild("timestamp")

        val firebaseConvAdapter = object : FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv::class.java,
                R.layout.users_single_layout,
                ConvViewHolder::class.java,
                conversationQuery
        ) {
            override fun populateViewHolder(convViewHolder: ConvViewHolder, conv: Conv, i: Int) {


                val listUserId = getRef(i).key

                val lastMessageQuery = mMessageDatabase!!.child(listUserId).limitToLast(1)

                lastMessageQuery.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                        val data = dataSnapshot.child("message").value!!.toString()
                        convViewHolder.setMessage(data, conv.isSeen)

                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })


                mUsersDatabase!!.child(listUserId).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val userName = dataSnapshot.child("name").value!!.toString()
                        val userThumb = dataSnapshot.child("thumb_image").value!!.toString()

                        if (dataSnapshot.hasChild("online")) {

                            val userOnline = dataSnapshot.child("online").value!!.toString()
                            convViewHolder.setUserOnline(userOnline)

                        }

                        convViewHolder.setName(userName)
                        try {
                            convViewHolder.setUserImage(userThumb, context!!)
                        } catch (e: KotlinNullPointerException) {

                        }


                        convViewHolder.mView.setOnClickListener {
                            val chatIntent = Intent(context, ChatActivity::class.java)
                            chatIntent.putExtra("user_id", listUserId)
                            chatIntent.putExtra("user_name", userName)
                            startActivity(chatIntent)
                        }


                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })

            }
        }

        mConvList!!.adapter = firebaseConvAdapter

    }

    class ConvViewHolder(internal var mView: View) : RecyclerView.ViewHolder(mView) {

        fun setMessage(message: String, isSeen: Boolean) {

            val userStatusView = mView.findViewById<View>(R.id.user_single_status) as TextView
            val indicator = mView.findViewById<View>(R.id.user_single_one_icon) as ImageView
            userStatusView.text = message

            if (!isSeen) {
                userStatusView.setTypeface(userStatusView.typeface, Typeface.BOLD)
                indicator.visibility = View.VISIBLE
            } else {
                indicator.visibility = View.GONE
                userStatusView.setTypeface(userStatusView.typeface, Typeface.NORMAL)
            }

        }

        fun setName(name: String) {

            val userNameView = mView.findViewById<View>(R.id.user_single_name) as TextView
            userNameView.text = name

        }

        fun setUserImage(thumb_image: String, ctx: Context) {

            val userImageView = mView.findViewById<View>(R.id.user_single_image) as CircleImageView
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.user).into(userImageView)

        }

        fun setUserOnline(online_status: String) {

            val userOnlineView = mView.findViewById<View>(R.id.user_single_online_icon) as ImageView

            if (online_status == "true") {

                userOnlineView.visibility = View.VISIBLE

            } else {

                userOnlineView.visibility = View.INVISIBLE

            }

        }


    }


}// Required empty public constructor
