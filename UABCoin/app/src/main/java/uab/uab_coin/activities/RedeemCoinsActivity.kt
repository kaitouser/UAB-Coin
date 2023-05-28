package uab.uab_coin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uab.uab_coin.R
import uab.uab_coin.adapter.OfferAdapter
import uab.uab_coin.databinding.ActivityRedeemCoinsBinding
import uab.uab_coin.models.OfferModel


class RedeemCoinsActivity : DrawerBaseActivity()
{
    var activityRedeemCoinsBinding: ActivityRedeemCoinsBinding? = null

    private lateinit var offerRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var offerList: ArrayList<OfferModel>

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRedeemCoinsBinding = ActivityRedeemCoinsBinding.inflate(layoutInflater)
        setContentView(activityRedeemCoinsBinding!!.root)

        offerRecyclerView = findViewById(R.id.rvOffers)
        offerRecyclerView.layoutManager = LinearLayoutManager(this)
        offerRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        offerList = arrayListOf<OfferModel>()

        userId = intent.getStringExtra("id").toString()

        getOffersData()

    }
    private fun getOffersData() {

        offerRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Offers")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offerList.clear()
                if (snapshot.exists()){
                    for (offerSnap in snapshot.children){
                        val offerData = offerSnap.getValue(OfferModel::class.java)
                        offerList.add(offerData!!)
                    }
                    val mAdapter = OfferAdapter(offerList)
                    offerRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : OfferAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@RedeemCoinsActivity, OfferActivity::class.java)
                            intent.putExtra("offerName", offerList[position].offerName)
                            intent.putExtra("offerPrice", offerList[position].offerPrice)
                            intent.putExtra("id", userId)
                            startActivity(intent)
                        }

                    })

                    offerRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}



