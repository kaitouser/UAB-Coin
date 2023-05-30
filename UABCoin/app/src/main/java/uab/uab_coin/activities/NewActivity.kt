package uab.uab_coin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import uab.uab_coin.R
import uab.uab_coin.models.UserModel
import kotlin.properties.Delegates

class NewActivity : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String
    private lateinit var newName : String
    private lateinit var newDescription : String
    private lateinit var newImage : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        userId = intent.getStringExtra("id").toString()
        newName = intent.getStringExtra("newName").toString()
        newDescription = intent.getStringExtra("newDescription").toString()
        newImage = intent.getStringExtra("newImage").toString()


        findViewById<TextView>(R.id.textNewName).text = newName
        findViewById<TextView>(R.id.textNewDescription).text = newDescription


        Glide.with(this)
            .load(newImage)
            .into(findViewById<ImageView>(R.id.NewImageDisplay))
    }
}