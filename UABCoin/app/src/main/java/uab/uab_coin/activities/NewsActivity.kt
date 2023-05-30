package uab.uab_coin.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import uab.uab_coin.adapter.NewAdapter
import uab.uab_coin.adapter.OfferAdapter
import uab.uab_coin.databinding.ActivityNewsBinding
import uab.uab_coin.models.NewModel

class NewsActivity : DrawerBaseActivity()
{
    var activityNewsBinding: ActivityNewsBinding? = null

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var newList: ArrayList<NewModel>

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewsBinding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(activityNewsBinding!!.root)

        newRecyclerView = findViewById(R.id.rvNews)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData2)

        newList = arrayListOf<NewModel>()

        userId = intent.getStringExtra("id").toString()

        getNewsData()

    }
    private fun getNewsData() {

        newRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("News")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newList.clear()
                if (snapshot.exists()){
                    for (newSnap in snapshot.children){
                        val newData = newSnap.getValue(NewModel::class.java)
                        newList.add(newData!!)
                    }
                    val mAdapter = NewAdapter(newList)
                    newRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : NewAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@NewsActivity, NewActivity::class.java)
                            intent.putExtra("newName", newList[position].newName)
                            intent.putExtra("newDescription", newList[position].newDescription)
                            intent.putExtra("newImage", newList[position].newImage)
                            intent.putExtra("id", userId)
                            startActivity(intent)
                        }
                    })

                    newRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
