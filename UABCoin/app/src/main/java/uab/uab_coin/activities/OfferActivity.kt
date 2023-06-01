package uab.uab_coin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import uab.uab_coin.R
import uab.uab_coin.models.UserModel
import java.util.Locale


class OfferActivity : AppCompatActivity()
{
    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String
    private lateinit var offerName : String
    private lateinit var offerPrice : String
    private lateinit var offerDescription : String
    private lateinit var offerRedeemCode : String
    private lateinit var offerImage : String

    private var alreadyRedeemed = "Waiting"

    private var sourceLanguageTitle = "CATALAN"
    private var targetLanguageTitle = Locale.getDefault().displayLanguage.uppercase()

    // Create an English-German translator:
    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceLanguageTitle).toString())
        .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLanguageTitle).toString())
        .build()
    var currentTranslator = Translation.getClient(options)


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)

        userId = intent.getStringExtra("id").toString()
        offerName = intent.getStringExtra("offerName").toString()
        offerPrice = intent.getStringExtra("offerPrice").toString()
        offerDescription = intent.getStringExtra("offerDescription").toString()
        offerRedeemCode = intent.getStringExtra("offerRedeemCode").toString()
        offerImage = intent.getStringExtra("offerImage").toString()

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        currentTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                currentTranslator.translate(offerDescription)
                    .addOnSuccessListener { translated_text ->
                        findViewById<TextView>(R.id.textOfferDescription).text = translated_text
                    }
                    .addOnFailureListener { exception ->
                        findViewById<TextView>(R.id.textOfferDescription).text = "Translation Error"
                    }
            }
            .addOnFailureListener { exception ->
                findViewById<TextView>(R.id.textOfferDescription).text = "Translation Packages Error"
            }

        findViewById<TextView>(R.id.textOfferName).text = offerName
        findViewById<TextView>(R.id.textOfferPrice).text = offerPrice

        Glide.with(this)
            .load(offerImage)
            .into(findViewById<ImageView>(R.id.offerImageDisplay))

        alreadyRedeemed = "No"


        findViewById<Button>(R.id.buttonRedeemOffer).setOnClickListener {
            checkRedeem()
            if (alreadyRedeemed=="No")
            {
                redeemOffer()
            }
            else
            {
                findViewById<TextView>(R.id.textOfferRedeemCode).text = offerRedeemCode + " (already redeemed)"
            }
        }
    }

    //
    private fun checkRedeem()
    {
        dbRef = FirebaseDatabase.getInstance().getReference("UsersRedeems").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        var key = snap.getKey()
                        if (key.toString().equals(offerRedeemCode)) {
                            alreadyRedeemed = "Yes"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    //
    private fun redeemOffer()
    {
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        if (alreadyRedeemed == "No") {
                            if (user.userCoins!! >= offerPrice.toInt()) {
                                dbRef = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId).child("userCoins")
                                dbRef.setValue(user.userCoins!! - offerPrice.toInt())
                                    .addOnCompleteListener {
                                        Toast.makeText(
                                            this@OfferActivity,
                                            "Reedem successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }.addOnFailureListener { err ->
                                        Toast.makeText(
                                            this@OfferActivity,
                                            "Error ${err.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                dbRef = FirebaseDatabase.getInstance().getReference("UsersRedeems")
                                    .child(userId).child(offerRedeemCode)
                                dbRef.setValue(offerPrice)

                                alreadyRedeemed = "Yes"

                                findViewById<TextView>(R.id.textOfferRedeemCode).text = offerRedeemCode
                            }
                            else
                            {
                                findViewById<TextView>(R.id.textOfferRedeemCode).text = "Not enough coins"
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}