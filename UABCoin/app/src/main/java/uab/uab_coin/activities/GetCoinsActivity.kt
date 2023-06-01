package uab.uab_coin.activities

import okhttp3.*
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import uab.uab_coin.R
import uab.uab_coin.R.id.btnTrash
import uab.uab_coin.adapter.OfferAdapter

import uab.uab_coin.databinding.ActivityGetCoinsBinding
import uab.uab_coin.models.OfferModel
import uab.uab_coin.models.UserModel
import java.io.ByteArrayOutputStream

class GetCoinsActivity : DrawerBaseActivity()
{

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    companion object {
        const val RESULT = "RESULT"
        private const val PICK_IMAGE_REQUEST = 1
        private const val CAMERA_REQUEST = 2
        private const val REQUEST_PERMISSION = 3
        private const val REQUEST_IMAGE_CAPTURE = 4
        private const val FUNCTION_URL = "https://europe-west3-uab-coin.cloudfunctions.net/function-1"
    }

    private fun initScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        //integrator.setPrompt("Sigue aprendiendo en CursoKotlin.com")
        integrator.setTorchEnabled(false)
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

                    if (result == "No") {
                        addCoins(getCode.toString(), coins.toString(), result)
                        Toast.makeText(this, coins + " coins redeemed", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Coins already claimed", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(imageBitmap)

            sendImageToCloudFunction(imageBitmap)
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun sendImageToCloudFunction(imageBitmap: Bitmap) {
        val url = "https://europe-west3-uab-coin.cloudfunctions.net/function-1"

        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "captured_image.jpg",
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Error al llamar a la función en la nube",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (responseData != null) {
                    val result = JSONObject(responseData)
                    val isTrash = result.getBoolean("isTrash")
                    val message = if (isTrash) "Es basura" else "No es basura"

                    runOnUiThread {
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Error al obtener la respuesta de la función en la nube",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private lateinit var binding: ActivityGetCoinsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetCoinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("id").toString()
        val btnTrash= findViewById<Button>(btnTrash)
        binding.btnTrash.setOnClickListener{
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
        binding.btnScan.setOnClickListener { initScanner() }


    }
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(imageBitmap)
        }
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
