package com.deetech.codechallenge

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

import org.w3c.dom.Text

import java.util.ArrayList

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {
    override fun getItemCount(): Int {
        return deals.size
    }

    internal var deals: ArrayList<TravelDeal>
    private val mFirebaseDatabase: FirebaseDatabase
    private val mDatabaseReference: DatabaseReference
    private val mChildListener: ChildEventListener
    private var imageDeal: ImageView? = null



    init {
        //FirebaseUtil.openFbReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase!!
        mDatabaseReference = FirebaseUtil.mDatabaseReference!!
        this.deals = FirebaseUtil.mDeals!!
        mChildListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val td = dataSnapshot.getValue(TravelDeal::class.java)
                //Log.d("Deal: ", td.title!!)
                if (td != null) {
                    td.id=dataSnapshot.key
                }
                if (td != null) {
                    deals.add(td)
                }
                notifyItemInserted(deals.size - 1)
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
        mDatabaseReference.addChildEventListener(mChildListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false)
        return DealViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals[position]
        holder.bind(deal)
    }

    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var tvTitle: TextView
        internal var tvDescription: TextView
        internal var tvPrice: TextView

        init {
            tvTitle = itemView.findViewById<View>(R.id.tvTitle) as TextView
            tvDescription = itemView.findViewById<View>(R.id.tvDescription) as TextView
            tvPrice = itemView.findViewById<View>(R.id.tvPrice) as TextView
            imageDeal = itemView.findViewById<View>(R.id.imageDeal) as ImageView
            itemView.setOnClickListener(this)
        }

        fun bind(deal: TravelDeal) {
            tvTitle.setText(deal.title)
            tvDescription.setText(deal.description)
            tvPrice.setText(deal.price)
            showImage(deal.imageUrl)
        }

        override fun onClick(view: View) {
            val position = getAdapterPosition()
            Log.d("Click", position.toString())
            val selectedDeal = deals[position]
            val intent = Intent(view.context, MainActivity::class.java)
            intent.putExtra("Deal", selectedDeal)
            view.context.startActivity(intent)
        }

        private fun showImage(url: String?) {
            if (url != null && url.isEmpty() == false) {
                Picasso.with(imageDeal!!.context)
                        .load(url)
                        .resize(160, 160)
                        .centerCrop()
                        .into(imageDeal)
            }
        }
    }
}
