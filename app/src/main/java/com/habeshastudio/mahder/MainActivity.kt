package com.habeshastudio.mahder

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.habeshastudio.mahder.activities.Dashboard
import com.habeshastudio.mahder.activities.SettingsActivity
import com.habeshastudio.mahder.activities.StartActivity
import com.habeshastudio.mahder.activities.UsersActivity
import com.habeshastudio.mahder.adapters.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mToolbar: Toolbar? = null
    private var mViewPager: ViewPager? = null
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mTabLayout: TabLayout? = null
    private var mUserRef: DatabaseReference? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val homeIntent = Intent(this@MainActivity, Dashboard::class.java)
                startActivity(homeIntent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                val profileIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(profileIntent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_dashboard

        mToolbar = findViewById<View>(R.id.main_page_toolbar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Chatting Pro"

        //tabs
        mViewPager = findViewById<View>(R.id.main_tab_pager) as ViewPager?
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager!!.adapter = mSectionsPagerAdapter
        mTabLayout = findViewById<View>(R.id.main_tabs) as TabLayout?
        mTabLayout!!.setupWithViewPager(mViewPager)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser

        if (currentUser == null) {
            sendToStart()
        } else {

            mUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid)
            mUserRef!!.child("online").setValue(true)
        }
    }

    private fun sendToStart() {
        val startIntent = Intent(this@MainActivity, StartActivity::class.java)
        startActivity(startIntent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == R.id.main_logout_btn) {

            mAuth!!.signOut()
            sendToStart()
        } else if (item.itemId == R.id.main_settings_btn) {
            val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsIntent)
        } else if (item.itemId == R.id.main_all_btn) {
            val usersIntent = Intent(this@MainActivity, UsersActivity::class.java)
            startActivity(usersIntent)
        }

        return true
    }
}
