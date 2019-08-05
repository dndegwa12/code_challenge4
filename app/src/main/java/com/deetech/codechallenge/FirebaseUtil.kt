package com.deetech.codechallenge

import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object FirebaseUtil {
    var mFirebaseDatabase: FirebaseDatabase? = null
    var mDatabaseReference: DatabaseReference? = null
    private var firebaseUtil: FirebaseUtil? = null
    var mFirebaseAuth: FirebaseAuth? = null
    var mStorage: FirebaseStorage? = null
    var mStorageRef: StorageReference? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    var mDeals: ArrayList<TravelDeal>? = null
    private val RC_SIGN_IN = 123
    private var caller: ListActivity? = null
    var isAdmin: Boolean = false


    fun openFbReference(ref: String, callerActivity: ListActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = FirebaseUtil
            mFirebaseDatabase = FirebaseDatabase.getInstance()
            mFirebaseAuth = FirebaseAuth.getInstance()
            caller = callerActivity

            mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null) {
                    FirebaseUtil.signIn()
                } else {
                    val userId = firebaseAuth.uid
                    checkAdmin(userId)
                }
                Toast.makeText(callerActivity.baseContext, "Welcome back!", Toast.LENGTH_LONG).show()
            }
            connectStorage()

        }

        mDeals = ArrayList()
        mDatabaseReference = this.mFirebaseDatabase?.reference?.child(ref)
    }

    private fun signIn() {
        // Choose authentication providers
        val providers = Arrays.asList(
                AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())

        // Create and launch sign-in intent
        caller!!.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN)
    }

    private fun checkAdmin(uid: String?) {
        FirebaseUtil.isAdmin = false
        val ref = mFirebaseDatabase!!.reference.child("administrators")
                .child(uid!!)
        val listener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                FirebaseUtil.isAdmin = true
                caller!!.showMenu()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        ref.addChildEventListener(listener)
    }

    fun attachListener() {
        mAuthListener?.let { mFirebaseAuth!!.addAuthStateListener(it) }
    }

    fun detachListener() {
        this!!.mAuthListener?.let { mFirebaseAuth!!.removeAuthStateListener(it) }
    }

    fun connectStorage() {
        mStorage = FirebaseStorage.getInstance()
        mStorageRef = mStorage!!.reference.child("deals_pictures")
    }
}