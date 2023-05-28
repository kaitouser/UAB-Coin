package uab.uab_coin.activities

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uab.uab_coin.R
import uab.uab_coin.models.UserModel

open class DrawerBaseActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    var drawerLayout: DrawerLayout? = null

    private lateinit var dbRef : DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var account: GoogleSignInAccount


    override fun setContentView(view: View) {
        auth = FirebaseAuth.getInstance()
        account = GoogleSignIn.getLastSignedInAccount(this)!!

        if (account != null) {
            fetchUser()
        }

        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base, null) as DrawerLayout
        val container = drawerLayout!!.findViewById<FrameLayout>(R.id.activityContainer)
        container.addView(view)
        super.setContentView(drawerLayout)
        val toolbar = drawerLayout!!.findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)
        val navigationView = drawerLayout!!.findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.menu_drawer_open,
            R.string.menu_drawer_close
        )
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout!!.closeDrawer(GravityCompat.START)
        if (item.itemId == R.id.nav_profile) {
            val intent : Intent  = Intent(this, ProfileActivity::class.java)
            if (account != null) {
                intent.putExtra("id", account.id)
            }
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else if (item.itemId == R.id.nav_logout) {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }
        return false
    }

    protected fun allocateActivityTitle(titleString: String?) {
        if (supportActionBar != null) {
            supportActionBar!!.title = titleString
        }
    }

    private fun fetchUser() {
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(account.id.toString())

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        findViewById<TextView>(R.id.textUserNameDrawer).text = user.userName.toString()
                    }
                }
            }
            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

