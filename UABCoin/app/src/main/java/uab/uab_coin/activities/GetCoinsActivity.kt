package uab.uab_coin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import uab.uab_coin.databinding.ActivityGetCoinsBinding
import uab.uab_coin.models.UserModel

class GetCoinsActivity : DrawerBaseActivity()
{

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    companion object {
        const val RESULT = "RESULT"
    }
    private fun initScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        //integrator.setPrompt("Sigue aprendiendo en CursoKotlin.com")
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                val inputString = result.contents.toString()
                val parts = inputString.split("/")
                val getCode = parts[0]
                val coins = parts[1]

                checkRedeem(getCode.toString()) { result ->

                    if (result == "No")
                    {
                        addCoins(getCode.toString(),coins.toString(), result)
                        Toast.makeText(this, coins + " coins redeemed", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this, "Coins already claimed", Toast.LENGTH_LONG).show()
                    }
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private lateinit var binding: ActivityGetCoinsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetCoinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("id").toString()

        binding.btnScan.setOnClickListener { initScanner() }


    }

    private fun checkRedeem(getCode : String, callback: (String) -> Unit)  {

        var redeemed = "No"

        dbRef = FirebaseDatabase.getInstance().getReference("UserGets").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        var key = snap.getKey()
                        if (key.toString().equals(getCode)) {
                            redeemed = "Yes"
                        }
                    }
                }
                callback(redeemed)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addCoins(getCode : String, coins : String, result : String) {
        var r = result

            dbRef =
                FirebaseDatabase.getInstance().getReference("UserGets").child(userId).child(getCode)
            dbRef.setValue(coins)
                .addOnCompleteListener {
                    //Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

                }.addOnFailureListener { err ->
                    //Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }

            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(UserModel::class.java)
                        if (user != null) {
                            dbRef =
                                FirebaseDatabase.getInstance().getReference("Users").child(userId)
                                    .child("userCoins")
                            if (r == "No") {
                                r = "Yes"
                                dbRef.setValue(user.userCoins?.toInt()?.plus(coins.toInt()))
                                    .addOnCompleteListener {
                                        //Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

                                    }.addOnFailureListener { err ->
                                        //Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
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
