package uab.uab_coin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import uab.uab_coin.R

class OfferActivity : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String
    private lateinit var offerName : String
    private lateinit var offerPrice : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)

        userId = intent.getStringExtra("id").toString()
        offerName = intent.getStringExtra("offerName").toString()
        offerPrice = intent.getStringExtra("offerPrice").toString()

        findViewById<TextView>(R.id.textOfferName).text = offerName
    }
}