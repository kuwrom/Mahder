package com.habeshastudio.mahder.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.habeshastudio.mahder.MainActivity
import com.habeshastudio.mahder.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private var mUserDatabase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null

    //Layout thing
    private var mDisplayImage: CircleImageView? = null
    private var mName: TextView? = null
    private var mStatus: TextView? = null
    private var mFollowers: TextView? = null
    private var mVotes: TextView? = null
    private var mAuth: FirebaseAuth? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mImageStorage: StorageReference? = null

    private var mStatusBtn: Button? = null
    private var mImageBtn: Button? = null
    private var mLogoutBtn: Button? = null


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val homeIntent = Intent(this@SettingsActivity, Dashboard::class.java)
                startActivity(homeIntent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                val messagesIntent = Intent(this@SettingsActivity, MainActivity::class.java)
                startActivity(messagesIntent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mAuth = FirebaseAuth.getInstance()

        mDisplayImage = findViewById<View>(R.id.settings_image) as CircleImageView
        mName = findViewById<View>(R.id.settings_display_name) as TextView
        mStatus = findViewById<View>(R.id.settings_status) as TextView
        mFollowers = findViewById<View>(R.id.settings_followers_count) as TextView
        mVotes = findViewById<View>(R.id.settings_votes_count) as TextView
        mStatusBtn = findViewById<View>(R.id.settings_status_button) as Button
        mLogoutBtn = findViewById<View>(R.id.profile_btn_logout) as Button
        mImageBtn = findViewById<View>(R.id.settings_image_btn) as Button

        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_notifications

        mImageStorage = FirebaseStorage.getInstance().reference


        mCurrentUser = FirebaseAuth.getInstance().currentUser

        val currentUid = mCurrentUser!!.uid

        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(currentUid)
        mUserDatabase!!.keepSynced(true)

        mUserDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val name = dataSnapshot.child("name").value!!.toString()
                val image = dataSnapshot.child("image").value!!.toString()
                val followers = dataSnapshot.child("followers").value!!.toString()
                val votes = dataSnapshot.child("votes").value!!.toString()
                val status = dataSnapshot.child("status").value!!.toString()
                val city = dataSnapshot.child("city").value!!.toString()
                val thumb_image = dataSnapshot.child("thumb_image").value!!.toString()

                mName!!.text = name
                mFollowers!!.text = followers
                mVotes!!.text = votes
                mStatus!!.text = (status + ", " + city)

                if (image != "default") {
                    //
                    //                    Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.user).into(mDisplayImage);
                    //
                    Picasso.with(this@SettingsActivity).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.user).into(mDisplayImage!!, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {

                            Picasso.with(this@SettingsActivity).load(image).placeholder(R.drawable.user).into(mDisplayImage)

                        }
                    })

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        mLogoutBtn!!.setOnClickListener {
            mAuth!!.signOut()
            val startIntent = Intent(this@SettingsActivity, StartActivity::class.java)
            startActivity(startIntent)
            finish()
        }
        mStatusBtn!!.setOnClickListener {
            val status_value = mStatus!!.text.toString()

            val status_intent = Intent(this@SettingsActivity, StatusActivity::class.java)
            status_intent.putExtra("status_value", status_value)

            startActivity(status_intent)
        }
        mImageBtn!!.setOnClickListener {
            //gallery image selector

            val gallary_intent = Intent()
            gallary_intent.type = "image/*"
            gallary_intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(gallary_intent, "Select Image"), gallery_pic)

            //library image selector

            //                CropImage.activity()
            //                        .setGuidelines(CropImageView.Guidelines.ON)
            //                        .start(SettingsActivity.this);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == Activity.RESULT_CANCELED)
            finish()

        if (requestCode == gallery_pic && resultCode == Activity.RESULT_OK) {

            val imageUri = data!!.data

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this)

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {


                mProgressDialog = ProgressDialog(this@SettingsActivity)
                mProgressDialog!!.setTitle("Uploading Image...")
                mProgressDialog!!.setMessage("Please wait while we upload and process the image.")
                mProgressDialog!!.setCanceledOnTouchOutside(false)
                mProgressDialog!!.show()


                val resultUri = result.uri

                val thumb_filePath = File(resultUri.path)

                val current_user_id = mCurrentUser!!.uid


                var thumb_bitmap: Bitmap? = null
                try {
                    thumb_bitmap = Compressor(this)
                            .setMaxWidth(128)
                            .setMaxHeight(128)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val baos = ByteArrayOutputStream()
                thumb_bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val thumb_byte = baos.toByteArray()


                val filepath = mImageStorage!!.child("profile_images").child("$current_user_id.jpg")
                val thumb_filepath = mImageStorage!!.child("profile_images").child("thumbs").child("$current_user_id.jpg")



                filepath.putFile(resultUri).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val download_url = task.result.downloadUrl!!.toString()

                        val uploadTask = thumb_filepath.putBytes(thumb_byte)
                        uploadTask.addOnCompleteListener { thumb_task ->
                            val thumb_downloadUrl = thumb_task.result.downloadUrl!!.toString()

                            if (thumb_task.isSuccessful) {

                                val update_hashMap = HashMap<String, Any>()
                                update_hashMap.put("image", download_url)
                                update_hashMap.put("thumb_image", thumb_downloadUrl)

                                mUserDatabase!!.updateChildren(update_hashMap).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        mProgressDialog!!.dismiss()
                                        Toast.makeText(this@SettingsActivity, "Success Uploading.", Toast.LENGTH_LONG).show()

                                    }
                                }


                            } else {

                                Toast.makeText(this@SettingsActivity, "Error in uploading thumbnail.", Toast.LENGTH_LONG).show()
                                mProgressDialog!!.dismiss()

                            }
                        }


                    } else {

                        Toast.makeText(this@SettingsActivity, "Error in uploading.", Toast.LENGTH_LONG).show()
                        mProgressDialog!!.dismiss()

                    }
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                val error = result.error

            } else {

            }
        }


    }

    companion object {

        private val gallery_pic = 1
    }

}


