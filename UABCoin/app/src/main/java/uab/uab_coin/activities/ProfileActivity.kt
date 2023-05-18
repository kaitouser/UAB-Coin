package uab.uab_coin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uab.uab_coin.R
import uab.uab_coin.models.UserModel
import javax.microedition.khronos.opengles.GL

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        userId = intent.getStringExtra("id").toString()

        fetchUser()

        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener {
            val intent : Intent  = Intent(this, WelcomeActivity::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonEdit).setOnClickListener {
            val intent : Intent  = Intent(this, EditProfileActivity::class.java)
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
                        findViewById<TextView>(R.id.textUserNameBig).text = user.userName.toString()
                        findViewById<TextView>(R.id.textUserName).text = user.userName.toString()
                        findViewById<TextView>(R.id.textEmail).text = user.userEmail.toString()
                        findViewById<TextView>(R.id.textToolbarCoins).text = user.userCoins.toString()

                        Glide.with(this@ProfileActivity)
                            .load(user.userPhoto.toString())
                            .into(findViewById<ImageView>(R.id.userImageDisplay))
                    }
                }
            }
            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}