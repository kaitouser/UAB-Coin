package uab.uab_coin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import uab.uab_coin.R
import java.util.Locale


class NewActivity : AppCompatActivity()
{
    private lateinit var userId : String
    private lateinit var newName : String
    private lateinit var newDescription : String
    private lateinit var newImage : String

    private var sourceLanguageTitle = "ca"
    private var targetLanguageTitle = Locale.getDefault().language

    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceLanguageTitle).toString())
        .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLanguageTitle).toString())
        .build()
    var currentTranslator = Translation.getClient(options)

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

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        currentTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                currentTranslator.translate(newDescription)
                    .addOnSuccessListener { translated_text ->
                        findViewById<TextView>(R.id.textNewDescription).text = translated_text
                    }
                    .addOnFailureListener { exception ->
                        findViewById<TextView>(R.id.textNewDescription).text = "Translation Error"
                    }
            }
            .addOnFailureListener { exception ->
                findViewById<TextView>(R.id.textNewDescription).text = "Translation Packages Error"
            }

        currentTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                currentTranslator.translate(newName)
                    .addOnSuccessListener { translated_text ->
                        findViewById<TextView>(R.id.textNewName).text = translated_text
                    }
                    .addOnFailureListener { exception ->
                        findViewById<TextView>(R.id.textNewName).text = "Translation Error"
                    }
            }
            .addOnFailureListener { exception ->
                findViewById<TextView>(R.id.textNewName).text = "Translation Packages Error"
            }

        // Modificar parametres noticia
        //findViewById<TextView>(R.id.textNewName).text = newName
        //findViewById<TextView>(R.id.textNewDescription).text = newDescription

        Glide.with(this)
            .load(newImage)
            .into(findViewById<ImageView>(R.id.NewImageDisplay))
    }
}