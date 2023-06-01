package uab.uab_coin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uab.uab_coin.R
import uab.uab_coin.models.UserModel
import uab.uab_coin.databinding.ActivityWelcomeBinding


class WelcomeActivity : DrawerBaseActivity()
{
    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    var activityWelcomeBinding: ActivityWelcomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Afegir barra d'eines
        super.onCreate(savedInstanceState)
        activityWelcomeBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(activityWelcomeBinding!!.root)

        // Obtenir informacio usuari
        auth = FirebaseAuth.getInstance()
        userId = intent.getStringExtra("id").toString()
        fetchUser()

        // Control boton "Obtain Coins"
        findViewById<Button>(R.id.buttonObtainCoins).setOnClickListener {
            val intent : Intent  = Intent(this, GetCoinsActivity::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        }

        // Control boton "Redeem Coins"
        findViewById<Button>(R.id.buttonRedeemCoins).setOnClickListener {
            val intent : Intent  = Intent(this, RedeemCoinsActivity::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        }

        // Control boton "Statistics"
        findViewById<Button>(R.id.buttonStatistics).setOnClickListener {
            val intent : Intent  = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        }

        // Control boton "News"
        findViewById<Button>(R.id.buttonNews).setOnClickListener {
            val intent : Intent  = Intent(this, NewsActivity::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        }

    }

    // Funcio per obtenir informacio del usuari a la Base de Dades
    private fun fetchUser()
    {
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        var actualText = findViewById<TextView>(R.id.textViewWelcomeUser).text.toString()
        var actualCoinText = findViewById<TextView>(R.id.textViewNCoins).text.toString()
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        findViewById<TextView>(R.id.textViewWelcomeUser).text = actualText + user.userName.toString() + "!"
                        findViewById<TextView>(R.id.textViewNCoins).text = user.userCoins.toString() + " " + actualCoinText
                    }
                }
            }
            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
