package uab.uab_coin.activities

import okhttp3.*
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
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
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import uab.uab_coin.R
import uab.uab_coin.R.id.btnTrash
import uab.uab_coin.adapter.OfferAdapter

import uab.uab_coin.databinding.ActivityGetCoinsBinding
import uab.uab_coin.models.OfferModel
import uab.uab_coin.models.UserModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        }else {
            //Toast.makeText(this, "Imagen no enviada", Toast.LENGTH_LONG).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun sendImageToCloudFunction(imageFile: File) {
        val url = "https://europe-west3-uab-coin.cloudfunctions.net/function-1"

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "captured_image.jpg",
                imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
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
                if (response.isSuccessful) {
                    val responseData = response.body?.string()

                    if (responseData != null) {
                        Log.d("ResponseData", responseData) // Imprimir responseData por pantalla
                        val result = JSONObject(responseData)
                        val labelsArray = result.getJSONArray("labels")
                        val labels = mutableListOf<String>()
                        for (i in 0 until labelsArray.length()) {
                            labels.add(labelsArray.getString(i))
                        }

                        runOnUiThread {
                            val message = if (labels.contains("trash")) "Es basura" else "No es basura"
                            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                            Log.d("Labels", labels.toString()) // Imprimir etiquetas en el log
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
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Error al llamar a la función en la nube",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        })
        // Log de las dimensiones de la imagen
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, options)
        val imageWidth = options.outWidth
        val imageHeight = options.outHeight
        Log.d("ImageDimensions", "Width: $imageWidth, Height: $imageHeight")
    }

    private lateinit var binding: ActivityGetCoinsBinding


    private var isImageCaptureRequested = false

    private lateinit var imageFile: File

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (isImageCaptureRequested && result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(imageBitmap)

            // Guardar la imagen en un archivo temporal
            imageFile = createImageFile()
            saveBitmapToFile(imageBitmap, imageFile)

            // Ahora que se ha capturado la imagen, envíala a la función de la nube
            sendImageToCloudFunction(imageFile)

            isImageCaptureRequested = false // Restablecer el indicador a false
        }
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Crear un nombre de archivo único basado en la fecha y hora actual
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetCoinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("id").toString()
        val btnTrash= findViewById<Button>(btnTrash)
        binding.btnTrash.setOnClickListener {
            // Establecer el indicador en true antes de iniciar la captura de imagen
            isImageCaptureRequested = true
            val captureIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startForResult.launch(captureIntent)
        }
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