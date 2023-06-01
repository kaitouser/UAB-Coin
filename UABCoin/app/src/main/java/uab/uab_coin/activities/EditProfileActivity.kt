package uab.uab_coin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import uab.uab_coin.R
import uab.uab_coin.databinding.ActivityEditProfileBinding


class EditProfileActivity : AppCompatActivity()
{
    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    private lateinit var etName: EditText
    private lateinit var etNiu: EditText

    private lateinit var btnEditName: ImageButton
    private lateinit var btnEditNiu: ImageButton

    var activityEditProfileBinding: ActivityEditProfileBinding? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Afegir barra d'eines
        super.onCreate(savedInstanceState)
        activityEditProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(activityEditProfileBinding!!.root)

        // Obtenir informacio usuari
        auth = FirebaseAuth.getInstance()
        userId = intent.getStringExtra("id").toString()

        etName = findViewById(R.id.etName)
        etNiu = findViewById(R.id.etNiu)
        btnEditName = findViewById(R.id.btnEditName)
        btnEditNiu = findViewById(R.id.btnEditNiu)

        // Funcions per l'edicio de parametres de l'usuari
        btnEditName.setOnClickListener {
            editName()
        }

        btnEditNiu.setOnClickListener {
            editNiu()
        }
    }

    // Funcio per editar el nom de l'usuari
    private fun editName() {
        val name = etName.text.toString()

        if (name.isEmpty()) {
            etName.error = "Please enter name"
        }
        else {
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userName")
            dbRef.setValue(name)
                .addOnCompleteListener {
                    Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()
                    etName.text.clear()

                }.addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Funcio per editar el niu de l'usuari
    private fun editNiu() {
        val niu = etNiu.text.toString()

        if (niu.isEmpty()) {
            etNiu.error = "Please enter niu"
        }
        else {
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userNiu")
            dbRef.setValue(niu)
                .addOnCompleteListener {
                    Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()
                    etNiu.text.clear()

                }.addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}