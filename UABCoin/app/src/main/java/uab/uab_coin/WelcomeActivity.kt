package uab.uab_coin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        val userName = intent.getStringExtra("name")
        findViewById<TextView>(R.id.textViewWelcomeUser).text = "Welcome " + userName.toString() + "!"
    }
}