package com.habeshastudio.mahder.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.library.bubbleview.BubbleLinearLayout
import com.google.firebase.database.*
import com.habeshastudio.mahder.R
import com.habeshastudio.mahder.model.Messages
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

/**
 * Created by kibrom kidane on 21/03/18
 */
class MessageAdapter(private val mMessageList: List<Messages>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private var mUserDatabase: DatabaseReference? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_single_layout, parent, false)

        return MessageViewHolder(v)

    }

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var messageText: TextView
        var timeText: TextView
        var seenView: ImageView
        var bubbleHolder: BubbleLinearLayout
        var profileImage: CircleImageView
        var displayName: TextView
        var messageImage: ImageView

        init {

            messageText = view.findViewById<View>(R.id.message_text_layout) as TextView
            bubbleHolder = view.findViewById<View>(R.id.bubble_message_holder) as BubbleLinearLayout
            profileImage = view.findViewById<View>(R.id.message_profile_layout) as CircleImageView
            displayName = view.findViewById<View>(R.id.name_text_layout) as TextView
            messageImage = view.findViewById<View>(R.id.message_image_layout) as ImageView
            seenView = view.findViewById<View>(R.id.seen_view) as ImageView
            timeText = view.findViewById<View>(R.id.time_text_layout) as TextView

        }
    }

    override fun onBindViewHolder(viewHolder: MessageViewHolder, i: Int) {

        val c = mMessageList[i]

        val from_user = c.from
        val message_type = c.type
        val message_time = c.time
        val message_is_seen = c.isSeen


        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(from_user!!)

        mUserDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val name = dataSnapshot.child("name").value!!.toString()
                val image = dataSnapshot.child("thumb_image").value!!.toString()


                viewHolder.displayName.text = name
//                viewHolder.bubbleHolder.


                Picasso.with(viewHolder.profileImage.context).load(image)
                        .placeholder(R.drawable.user).into(viewHolder.profileImage)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        if (message_type == "text") {

            viewHolder.messageText.text = c.message
            viewHolder.messageImage.visibility = View.INVISIBLE


            val dateFormat = SimpleDateFormat("hh:mm a")
            val date = message_time
            if (c.isSeen)
                viewHolder.seenView.setBackgroundResource(R.drawable.seen)
            viewHolder.timeText.text = dateFormat.format(date).toString()


        } else {

            viewHolder.messageText.visibility = View.INVISIBLE
            Picasso.with(viewHolder.profileImage.context).load(c.message)
                    .placeholder(R.drawable.user).into(viewHolder.messageImage)
            viewHolder.timeText.text = message_time.toString()

        }

    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }


}
