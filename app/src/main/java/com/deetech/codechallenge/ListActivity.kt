package com.deetech.codechallenge

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.*
import androidx.recyclerview.widget.RecyclerView

import com.firebase.ui.auth.AuthUI

import java.util.ArrayList

open class ListActivity : AppCompatActivity() {
    /*ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;*/
    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.list_activity_menu, menu)
        val insertMenu = menu.findItem(R.id.insert_menu)
        insertMenu.isVisible = FirebaseUtil.isAdmin === true



        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.insert_menu -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.delete_menu -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener {
                            Log.d("Logout", "User Logged Out")
                            FirebaseUtil.attachListener()
                        }
                FirebaseUtil.detachListener()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override protected fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    @SuppressLint("WrongConstant")
    override protected fun onResume() {
        super.onResume()
        FirebaseUtil.openFbReference("traveldeals", this)
        val rvDeals = findViewById<RecyclerView>(R.id.rvDeals)
        val adapter = DealAdapter()
        rvDeals.setAdapter(adapter)
        val dealsLayoutManager = LinearLayoutManager(this, VERTICAL, false)
        rvDeals.setLayoutManager(dealsLayoutManager)
        FirebaseUtil.attachListener()
    }

    fun showMenu() {
        invalidateOptionsMenu()
    }
}
