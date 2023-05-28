package uab.uab_coin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uab.uab_coin.R
import uab.uab_coin.databinding.ActivityEditProfileBinding
import uab.uab_coin.databinding.ActivityWelcomeBinding
import uab.uab_coin.models.UserModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    private lateinit var etName: EditText
    private lateinit var btnEditName: ImageButton

    var activityEditProfileBinding: ActivityEditProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(activityEditProfileBinding!!.root)

        auth = FirebaseAuth.getInstance()

        userId = intent.getStringExtra("id").toString()

        etName = findViewById(R.id.etName)
        btnEditName = findViewById(R.id.btnEditName)

        //fetchUser()

        btnEditName.setOnClickListener {
            editName()
        }

    }

    private fun fetchUser() {
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        findViewById<TextView>(R.id.textToolbarCoins).text = user.userCoins.toString()
                    }
                }
            }
            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

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
}