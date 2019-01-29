package com.habeshastudio.mahder.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.habeshastudio.mahder.MainActivity
import com.habeshastudio.mahder.R
import java.util.*

class FormActivity : AppCompatActivity() {

    private var mDisplayName: TextInputLayout? = null
    private var mProfession: TextInputLayout? = null
    internal var spinnerCities: Spinner? = null
    private var mCreateButton: Button? = null

    internal var cities = arrayOf("Abiy Addi", "Adama (Nazareth/Nazret)", "Adaba", "Addis Ababa", "Addis Alem (Ejersa)", "Addis Zemen",
            "Adet", "Adigrat", "Adwa", "Agaro", "Akaki", "Alaba (Kulito Quliito)", "Alitena", "Amaro", "Amba Mariam", "Ambo", "Ankober",
            "Arba Minch", "Arboye", "Asaita", "Asella", "Asosa", "Awasa", "Awash", "Axum", "Alamata", "Alem Ketema", "Aykel", "Babille",
            "Baco", "Badme", "Bahir Dar", "Bati", "Bedele", "Beica", "Bichena", "Bishoftu", "Bonga", "Burie Damote", "Butajira", "Ciro",
            "Chencha", "Chuahit", "chelenko", "Dabat", "Dangila", "Debarq", "Debre Berhan", "Debre Marqos", "Debre Tabor", "Debre Werq",
            "Debre Zebit", "Dejen", "Delgi", "Dembidolo", "Dessie (Dese)", "Dila", "DilYibza (Beyeda)", "Dire Dawa", "Dirree Incinnii",
            "Dodola", "Dolo Bay", "Dolo Odo", "Durame", "Dalocha", "Dukem", "Finicha'a", "Fiche", "Finote Selam", "Freweyni", "Gambela",
            "Gelemso", "Ghimbi", "Ginir", "Goba", "Gobiyere", "Gode", "Gololcha", "Gondar", "Gongoma", "Gore", "Gorgora", "Harar (or Harer)",
            "Harorisa", "Hayq", "Holeta", "Hosaena", "Humera", "Huruta", "Imi", "ijiga", "Jimma", "Kabri Dar", "Kebri Mangest", "Kobo",
            "Kombolcha", "Lalibela", "Limmu Genet", "Maji", "Maychew", "Mek'ele", "Mendi", "Metemma", "Metu", " Mizan Teferi", "Mojo",
            "Mota", "Moyale", "Negash", "Negele Boran", "Nekemte", "Robe", "Bale", "Shashamane", "Shire (or Inda Selassie)", "Shilavo",
            "Sodo (Wolaita Sodo)", "Sodore", "Sokoru", "Tefki", "Tenta", "Tippi", "Tiya", "Tullu Milki", "Turmi", "Togo-Wochale", "Wacca",
            "Walwal", "Werder", "Wereta", "Woldia", " Wolaita Sodo (Sodo)", "Waliso", "Wolleka", "Wuchale", "Worabe", "Yabelo", "Yeha",
            "Yirga Alem", "Ziway")

//    internal var professionList = arrayListOf("Professors", "Teacher", "Actor", "Musician", "Philosopher", "Visual Artist", "Writer", "Audiologist",
//            "Chiropractor", "Dentist", "Farmer", "Doctor", "Midwive", "Nurse", Occupational therapists Optometrists
//            Pathologists Pharmacists Physical therapists Physicians Psychologists
//            Speech-language pathologists Accountants Actuaries Agriculturists Architects Economists Engineers
//            Interpreters Attorney Advocates Solicitors Librarians Statisticians Surveyors Urban planners Human resources Firefighters Judges
//            Military officers Police officers Air traffic controllers Aircraft pilots Sea captains Scientists Astronomers Biologists
//            Botanists Ecologists Geneticists Immunologists Pharmacologists Virologists Zoologists Chemists Geologists
//            Meteorologists Oceanographers Physicists Programmers Web developers Designers Graphic designers Web designers)


    //firebase
    private var mCurrentUser: FirebaseUser? = null
    private var mUserDatabase: DatabaseReference? = null
    private var mDoctorsDatabase: DatabaseReference? = null
    private var mEngineersDatabase: DatabaseReference? = null
    private var mLawyersDatabase: DatabaseReference? = null
    private var mTechniciansDatabase: DatabaseReference? = null
    private var mTeachersDatabase: DatabaseReference? = null
    private var mMerchantsDatabase: DatabaseReference? = null
    private var mDriversDatabase: DatabaseReference? = null
    private var mArtistsDatabase: DatabaseReference? = null
    private var mToolbar: Toolbar? = null

    //progress dialog
    private var mRegProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        mToolbar = findViewById<View>(R.id.form_page_toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mRegProgress = ProgressDialog(this)

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        val currentUid = mCurrentUser!!.uid


        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        //list of professionals
        mDoctorsDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Doctors")
        mEngineersDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Engineers")
        mLawyersDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Lawyers")
        mTechniciansDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Technicians")
        mTeachersDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Teachers")
        mMerchantsDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Merchants")
        mDriversDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Drivers")
        mArtistsDatabase = FirebaseDatabase.getInstance().reference.child("Professionals").child("Artists")

        spinnerCities = findViewById<View>(R.id.cities_spinner) as Spinner
        mDisplayName = findViewById<View>(R.id.name_form) as TextInputLayout
        mProfession = findViewById<View>(R.id.profession_form) as TextInputLayout
        mCreateButton = findViewById<View>(R.id.proceed_button) as Button

        val adapter = ArrayAdapter(this@FormActivity, android.R.layout.simple_spinner_item, cities)//setting the country_array to spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCities!!.adapter = adapter

        mCreateButton!!.setOnClickListener {
            val mCity = spinnerCities!!.selectedItem.toString()
            register_user(mDisplayName!!.editText!!.text.toString(), mProfession!!.editText!!.text.toString(), mCity)
        }

    }

    private fun register_user(name: String, profession: String, city: String) {

        mRegProgress!!.setTitle("Registering ...")
        mRegProgress!!.setMessage("Please wait while we create your account")
        mRegProgress!!.setCanceledOnTouchOutside(false)
        mRegProgress!!.show()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uId = currentUser!!.uid

        val userMap = HashMap<String, String>()
        userMap["name"] = name
        userMap["status"] = profession
        userMap["votes"] = "0"
        userMap["answers"] = "0"
        userMap["followers"] = "0"
        userMap["city"] = city
        userMap["image"] = "default"
        userMap["thumb_image"] = "default"
        mUserDatabase!!.child(uId).setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {


                if (profession.equals("Doctor")) {
                    mDoctorsDatabase!!.child(uId).child("name").setValue(name)
                    mDoctorsDatabase!!.child(uId).child("thumb_image").setValue("default")
                    mDoctorsDatabase!!.child(uId).child("status").setValue(profession)
                } else if (profession.equals("Engineer")) {
                    mEngineersDatabase!!.child(uId).child("name").setValue(name)
                    mEngineersDatabase!!.child(uId).child("thumb_image").setValue("default")
                    mEngineersDatabase!!.child(uId).child("status").setValue(profession)
                } else if (profession.equals("Lawyer")) {
                    mLawyersDatabase!!.child(uId).child("name").setValue(name)
                    mLawyersDatabase!!.child(uId).child("thumb_image").setValue("default")
                    mLawyersDatabase!!.child(uId).child("status").setValue(profession)
                } else if (profession.equals("Technician")) {
                    mTechniciansDatabase!!.child(uId).child("name").setValue(name)
                    mTechniciansDatabase!!.child(uId).child("thumb_image").setValue("default")
                    mTechniciansDatabase!!.child(uId).child("status").setValue(profession)
                } else if (profession.equals("Teacher")) {
                    mTeachersDatabase!!.child(uId).child("name").setValue(name)
                    mTeachersDatabase!!.child(uId).child("thumb_image").setValue("default")
                    mTeachersDatabase!!.child(uId).child("status").setValue(profession)
                } else if (profession.equals("Merchant")) {
                    mMerchantsDatabase!!.child(uId).child("name").setValue(name)
                    mMerchantsDatabase!!.child(uId).child("thumb_image").setValue("default")
                    mMerchantsDatabase!!.child(uId).child("status").setValue(profession)
                } else if (profession.equals("Driver")) {
                    mDriversDatabase!!.child(uId).child("name").setValue(name)
                    mDriversDatabase!!.child(uId).child("thumb_image").setValue("default")
                    mDriversDatabase!!.child(uId).child("status").setValue(profession)
                } else if (profession.equals("Artist")) {
                    mArtistsDatabase!!.child(uId).child("name").setValue(name)
                    mArtistsDatabase!!.child(uId).child("thumb_image").setValue("default")
                    mArtistsDatabase!!.child(uId).child("status").setValue(profession)
                }


                mRegProgress!!.dismiss()
                val mainIntent = Intent(this@FormActivity, MainActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            }
        }

        // ...
    }
}

