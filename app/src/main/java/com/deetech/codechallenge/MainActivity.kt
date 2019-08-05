package com.deetech.codechallenge

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    internal var txtTitle: EditText? = null
    internal var txtDescription: EditText? = null
    internal var txtPrice: EditText? = null
    internal var imageView: ImageView? = null
    internal var deal: TravelDeal? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        txtTitle = findViewById(R.id.txtTitle) as EditText
        txtDescription = findViewById(R.id.txtDescription) as EditText
        txtPrice = findViewById(R.id.txtPrice) as EditText
        imageView = findViewById(R.id.image) as ImageView
        val intent = getIntent()
        var deal = intent.getSerializableExtra("Deal") as TravelDeal
        if (deal == null) {
            deal = TravelDeal()
        }
        this.deal = deal
        txtTitle!!.setText(deal.title)
        txtDescription!!.setText(deal.description)
        txtPrice!!.setText(deal.price)
        showImage(deal.imageUrl)
        val btnImage = this.findViewById<Button>(R.id.btnImage)
        btnImage.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(intent.createChooser(intent,
                    "Insert Picture"), PICTURE_RESULT)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveDeal()
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show()
                clean()
                backToList()
                return true
            }
            R.id.delete_menu -> {
                deleteDeal()
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show()
                backToList()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = getMenuInflater()
        inflater.inflate(R.menu.save_menu, menu)
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).isVisible = true
            menu.findItem(R.id.save_menu).isVisible = true
            enableEditTexts(true)
        } else {
            menu.findItem(R.id.delete_menu).isVisible = false
            menu.findItem(R.id.save_menu).isVisible = false
            enableEditTexts(false)
        }


        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            val imageUri = data.data
            val ref = FirebaseUtil.mStorageRef.child(imageUri!!.lastPathSegment)
            ref.putFile(imageUri).addOnSuccessListener(this, OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                val url = taskSnapshot.getDownloadUrl().toString()
                val pictureName = taskSnapshot.storage.path
                deal!!.imageUrl=url
                deal!!.imageName=pictureName
                Log.d("Url: ", url)
                Log.d("Name", pictureName)
                showImage(url)
            })

        }
    }

    private fun saveDeal() {
        deal!!.title=txtTitle.toString()
        deal!!.description=txtDescription.toString())
        deal!!.id=txtPrice.toString()
        if (deal!!.id == null) {
            mDatabaseReference!!.push().setValue(deal)
        } else {
            mDatabaseReference!!.child(deal!!.id!!).setValue(deal)
        }
    }

    private fun deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show()
            return
        }
        mDatabaseReference!!.child(deal!!.id!!).removeValue()
        Log.d("image name", deal!!.imageName)
        if (deal!!.imageName != null && deal!!.imageName.isEmpty() === false) {
            val picRef = FirebaseUtil.mStorage.getReference().child(deal!!.imageName!!)
            picRef.delete().addOnSuccessListener { Log.d("Delete Image", "Image Successfully Deleted") }.addOnFailureListener { e -> Log.d("Delete Image", e.message) }
        }

    }

    private fun backToList() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    private fun clean() {
        txtTitle.setText("")
        txtPrice.setText("")
        txtDescription.setText("")
        txtTitle.requestFocus()
    }

    private fun enableEditTexts(isEnabled: Boolean) {
        txtTitle.isEnabled = isEnabled
        txtDescription.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
    }

    private fun showImage(url: String?) {
        if (url != null && url.isEmpty() == false) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            Picasso.with(this)
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView)
        }
    }

    companion object {
        private val PICTURE_RESULT = 42 //the answer to everything
    }

}
