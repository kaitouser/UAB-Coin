package uab.uab_coin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import uab.uab_coin.R


class NewActivity : AppCompatActivity()
{
    private lateinit var userId : String
    private lateinit var newName : String
    private lateinit var newDescription : String
    private lateinit var newImage : String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Establir arxiu layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        // Obtenir informacio noticia
        userId = intent.getStringExtra("id").toString()
        newName = intent.getStringExtra("newName").toString()
        newDescription = intent.getStringExtra("newDescription").toString()
        newImage = intent.getStringExtra("newImage").toString()

        // Modificar parametres noticia
        findViewById<TextView>(R.id.textNewName).text = newName
        findViewById<TextView>(R.id.textNewDescription).text = newDescription

        Glide.with(this)
            .load(newImage)
            .into(findViewById<ImageView>(R.id.NewImageDisplay))
    }
}