package uab.uab_coin.activities

import HistoricAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uab.uab_coin.R
import uab.uab_coin.adapter.OfferAdapter
import uab.uab_coin.databinding.ActivityStatisticsBinding
import uab.uab_coin.models.OfferModel


class StatisticsActivity : DrawerBaseActivity()
{
    var activityStatisticsBinding: ActivityStatisticsBinding? = null

    private lateinit var statRecyclerView: RecyclerView
    private lateinit var statLoadingData: TextView

    private lateinit var dbRef : DatabaseReference

    private lateinit var shopList: ArrayList<String>

    private lateinit var userId : String
    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Afegir barra d'eines
        super.onCreate(savedInstanceState)
        activityStatisticsBinding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(activityStatisticsBinding!!.root)

        statRecyclerView = findViewById(R.id.bought_Offers)
        statRecyclerView.layoutManager = LinearLayoutManager(this)
        statRecyclerView.setHasFixedSize(true)
        statLoadingData = findViewById(R.id.b_offer_LoadingData)

        shopList = arrayListOf<String>()

        userId = intent.getStringExtra("id").toString()
        getHistoricData()
    }

    private fun getHistoricData() {
        statRecyclerView.visibility = View.GONE
        statLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("UsersRedeems").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        var keyvalue = snap.getValue()
                        shopList.add("-" + keyvalue.toString())
                    }
                    val shopAdapter = HistoricAdapter(shopList)
                    statRecyclerView.adapter = shopAdapter

                    statRecyclerView.visibility = View.VISIBLE
                    statLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        dbRef = FirebaseDatabase.getInstance().getReference("UserGets").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        var keyvalue = snap.getValue()
                        shopList.add("+" + keyvalue.toString())
                    }
                    val shopAdapter = HistoricAdapter(shopList)
                    statRecyclerView.adapter = shopAdapter

                    statRecyclerView.visibility = View.VISIBLE
                    statLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}