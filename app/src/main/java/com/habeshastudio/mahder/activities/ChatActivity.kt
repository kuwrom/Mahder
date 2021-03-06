package com.habeshastudio.mahder.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.habeshastudio.mahder.R
import com.habeshastudio.mahder.adapters.MessageAdapter
import com.habeshastudio.mahder.model.GetTimeAgo
import com.habeshastudio.mahder.model.Messages
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var mChatUser: String? = null
    private var mChatToolbar: Toolbar? = null

    private var mRootRef: DatabaseReference? = null

    private var mTitleView: TextView? = null
    private var mLastSeenView: TextView? = null
    private var mProfileImage: CircleImageView? = null
    private var mAuth: FirebaseAuth? = null
    private var mCurrentUserId: String? = null

    private var mChatAddBtn: ImageButton? = null
    private var mChatSendBtn: ImageButton? = null
    private var mChatMessageView: EditText? = null

    private var mMessagesList: RecyclerView? = null
    private var mRefreshLayout: SwipeRefreshLayout? = null

    private val messagesList = ArrayList<Messages>()
    private var mLinearLayout: LinearLayoutManager? = null
    private var mAdapter: MessageAdapter? = null
    private var mCurrentPage = 1

    // Storage Firebase
    private var mImageStorage: StorageReference? = null


    //New Solution
    private var itemPos = 0

    private var mLastKey = ""
    private var mPrevKey = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mChatToolbar = findViewById<View>(R.id.chat_app_bar) as Toolbar
        setSupportActionBar(mChatToolbar)

        val actionBar = supportActionBar

        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowCustomEnabled(true)

        mRootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        mCurrentUserId = mAuth!!.currentUser!!.uid

        mChatUser = intent.getStringExtra("user_id")
        val userName = intent.getStringExtra("user_name")

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null)

        actionBar.customView = action_bar_view

        // ---- Custom Action bar Items ----

        mTitleView = findViewById<View>(R.id.custom_bar_title) as TextView
        mLastSeenView = findViewById<View>(R.id.custom_bar_seen) as TextView
        mProfileImage = findViewById<View>(R.id.custom_bar_image) as CircleImageView

        mChatAddBtn = findViewById<View>(R.id.chat_add_btn) as ImageButton
        mChatSendBtn = findViewById<View>(R.id.chat_send_btn) as ImageButton
        mChatMessageView = findViewById<View>(R.id.chat_message_view) as EditText

        mAdapter = MessageAdapter(messagesList)

        mMessagesList = findViewById<View>(R.id.messages_list) as RecyclerView
        mRefreshLayout = findViewById<View>(R.id.message_swipe_layout) as SwipeRefreshLayout
        mLinearLayout = LinearLayoutManager(this)

        mMessagesList!!.setHasFixedSize(true)
        mMessagesList!!.layoutManager = mLinearLayout

        mMessagesList!!.adapter = mAdapter

        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().reference

        mRootRef!!.child("Chat").child(mCurrentUserId!!).child(mChatUser!!).child("seen").setValue(true)


        loadMessages()

        mTitleView!!.text = userName

        mRootRef!!.child("Users").child(mChatUser!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val online = dataSnapshot.child("online").value!!.toString()
                val image = dataSnapshot.child("image").value!!.toString()

                if (online == "true") {

                    mLastSeenView!!.text = "Online"

                } else {

                    //GetTimeAgo getTimeAgo = new GetTimeAgo();

                    val lastTime = java.lang.Long.parseLong(online)

                    val lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, applicationContext)

                    mLastSeenView!!.text = lastSeenTime

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


        mRootRef!!.child("Chat").child(mCurrentUserId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser!!)) {

                    var chatAddMap = HashMap<String, Any>()
                    chatAddMap.put("seen", false)
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP)

                    var chatUserMap = HashMap<String, Any>()
                    chatUserMap.put("Chat/$mCurrentUserId/$mChatUser", chatAddMap)
                    chatUserMap.put("Chat/$mChatUser/$mCurrentUserId", chatAddMap)

                    mRootRef!!.updateChildren(chatUserMap) { databaseError, databaseReference ->
                        if (databaseError != null) {

                            Log.d("CHAT_LOG", databaseError.message.toString())

                        }
                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })



        mChatSendBtn!!.setOnClickListener { sendMessage() }



        mChatAddBtn!!.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK)
        }



        mRefreshLayout!!.setOnRefreshListener {
            mCurrentPage++

            itemPos = 0

            loadMoreMessages()
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Activity.RESULT_CANCELED)
            finish()

        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK) {

            val imageUri = data!!.data

            val current_user_ref = "messages/$mCurrentUserId/$mChatUser"
            val chat_user_ref = "messages/$mChatUser/$mCurrentUserId"

            val user_message_push = mRootRef!!.child("messages")
                    .child(mCurrentUserId!!).child(mChatUser!!).push()

            val push_id = user_message_push.key


            val filepath = mImageStorage!!.child("message_images").child("$push_id.jpg")

            filepath.putFile(imageUri!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val download_url = task.result.downloadUrl!!.toString()


                    val messageMap = HashMap<String, Any>()
                    messageMap.put("message", download_url)
                    messageMap.put("seen", false)
                    messageMap.put("type", "image")
                    messageMap.put("time", ServerValue.TIMESTAMP)
                    messageMap.put("from", mCurrentUserId!!)

                    val messageUserMap = HashMap<String, Any>()
                    messageUserMap.put("$current_user_ref/$push_id", messageMap)
                    messageUserMap.put("$chat_user_ref/$push_id", messageMap)

                    mChatMessageView!!.setText("")

                    mRootRef!!.updateChildren(messageUserMap) { databaseError, databaseReference ->
                        if (databaseError != null) {

                            Log.d("CHAT_LOG", databaseError.message.toString())

                        }
                    }


                }
            }

        }

    }

    private fun loadMoreMessages() {

        val messageRef = mRootRef!!.child("messages").child(mCurrentUserId!!).child(mChatUser!!)

        val messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10)

        messageQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {


                val message = dataSnapshot.getValue(Messages::class.java)
                val messageKey = dataSnapshot.key

                if (mPrevKey != messageKey) {

                    messagesList.add(itemPos++, message!!)

                } else {

                    mPrevKey = mLastKey

                }


                if (itemPos == 1) {

                    mLastKey = messageKey

                }


                Log.d("TOTALKEYS", "Last Key : $mLastKey | Prev Key : $mPrevKey | Message Key : $messageKey")

                mAdapter!!.notifyDataSetChanged()

                mRefreshLayout!!.isRefreshing = false

                mLinearLayout!!.scrollToPositionWithOffset(10, 0)

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

    }

    private fun loadMessages() {

        val messageRef = mRootRef!!.child("messages").child(mCurrentUserId!!).child(mChatUser!!)

        val messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD)


        messageQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                val message = dataSnapshot.getValue(Messages::class.java)

                itemPos++

                if (itemPos == 1) {

                    val messageKey = dataSnapshot.key

                    mLastKey = messageKey
                    mPrevKey = messageKey

                }

                messagesList.add(message!!)
                mAdapter!!.notifyDataSetChanged()

                mMessagesList!!.scrollToPosition(messagesList.size - 1)

                mRefreshLayout!!.isRefreshing = false

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

    }

    private fun sendMessage() {


        val message = mChatMessageView!!.text.toString()

        if (!TextUtils.isEmpty(message)) {

            val current_user_ref = "messages/$mCurrentUserId/$mChatUser"
            val chat_user_ref = "messages/$mChatUser/$mCurrentUserId"

            val user_message_push = mRootRef!!.child("messages")
                    .child(mCurrentUserId!!).child(mChatUser!!).push()

            val push_id = user_message_push.key

            val messageMap = HashMap<String, Any>()
            messageMap.put("message", message)
            messageMap.put("seen", false)
            messageMap.put("type", "text")
            messageMap.put("time", ServerValue.TIMESTAMP)
            messageMap.put("from", mCurrentUserId!!)

            val messageUserMap = HashMap<String, Any>()
            messageUserMap.put("$current_user_ref/$push_id", messageMap)
            messageUserMap.put("$chat_user_ref/$push_id", messageMap)

            mChatMessageView!!.setText("")

            mRootRef!!.child("Chat").child(mCurrentUserId!!).child(mChatUser!!).child("seen").setValue(true)
            mRootRef!!.child("Chat").child(mCurrentUserId!!).child(mChatUser!!).child("timestamp").setValue(ServerValue.TIMESTAMP)

            mRootRef!!.child("Chat").child(mChatUser!!).child(mCurrentUserId!!).child("seen").setValue(false)
            mRootRef!!.child("Chat").child(mChatUser!!).child(mCurrentUserId!!).child("timestamp").setValue(ServerValue.TIMESTAMP)

            mRootRef!!.updateChildren(messageUserMap) { databaseError, databaseReference ->
                if (databaseError != null) {

                    Log.d("CHAT_LOG", databaseError.message.toString())

                }
            }

        }

    }

    companion object {

        private val TOTAL_ITEMS_TO_LOAD = 10

        private val GALLERY_PICK = 1
    }
}
