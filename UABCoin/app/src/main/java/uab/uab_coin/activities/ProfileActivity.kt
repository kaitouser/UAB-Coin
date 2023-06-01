package uab.uab_coin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import uab.uab_coin.databinding.ActivityProfileBinding
import uab.uab_coin.models.UserModel


class ProfileActivity : DrawerBaseActivity()
{
    var activityProfileBinding: ActivityProfileBinding? = null

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var userId : String


    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Afegir barra d'eines
        super.onCreate(savedInstanceState)
        activityProfileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(activityProfileBinding!!.root)

        // Obtenir informacio usuari
        auth = FirebaseAuth.getInstance()
        userId = intent.getStringExtra("id").toString()
        fetchUser()

        // Control boton "Edit"
        findViewById<Button>(R.id.buttonEdit).setOnClickListener {
            val intent : Intent  = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        }
    }

    // Funcio per obtenir informacio del usuari a la Base de Dades
    private fun fetchUser()
    {
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        findViewById<TextView>(R.id.textUserNameBig).text = user.userName.toString()
                        findViewById<TextView>(R.id.textUserName).text = user.userName.toString()
                        findViewById<TextView>(R.id.textEmail).text = user.userEmail.toString()
                        findViewById<TextView>(R.id.textNiu).text = user.userNiu.toString()

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