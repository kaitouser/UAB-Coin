package uab.uab_coin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout

import android.content.Intent
import android.widget.Button

class WelcomeActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Go to Activity
        findViewById<Button>(R.id.getCoinsBtn).setOnClickListener {
            startActivity(Intent(this, GetCoinsActivity::class.java))
        }
        findViewById<Button>(R.id.redeemCoinsBtn).setOnClickListener {
            startActivity(Intent(this, RedeemCoinsActivity::class.java))
        }
        findViewById<Button>(R.id.statsBtn).setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }

        // Variables BD
        val userName = intent.getStringExtra("name")
        findViewById<TextView>(R.id.textViewWelcomeUser).text = "Welcome " + userName.toString() + "!"

        // Drawer Layout
        drawerLayout = findViewById(R.id.navMenuId)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Function Drawer Layout
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }


}