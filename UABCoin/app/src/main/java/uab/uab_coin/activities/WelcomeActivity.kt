package uab.uab_coin.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import uab.uab_coin.R
import uab.uab_coin.models.UserModel

class WelcomeActivity : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()

        userId = intent.getStringExtra("id").toString()

        fetchUser()

        findViewById<Button>(R.id.buttonSignOut).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.buttonProfile).setOnClickListener {
            val intent : Intent  = Intent(this, ProfileActivity::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        }
    }

    private fun fetchUser() {
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        findViewById<TextView>(R.id.textViewWelcomeUser).text = "Welcome " + user.userName.toString() + "!"
                        findViewById<TextView>(R.id.textViewNCoins).text = user.userCoins.toString() + "coins"
                    }
                }
            }
            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}