@file:Suppress("DEPRECATION")

package uab.uab_coin.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import uab.uab_coin.R
import uab.uab_coin.databinding.ActivitySettingsBinding
import java.util.Locale


class SettingsActivity  : DrawerBaseActivity()
{
    var activitySettingsBinding: ActivitySettingsBinding? = null

    private lateinit var userId : String


    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Afegir barra d'eines
        super.onCreate(savedInstanceState)
        activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding!!.root)

        // Obtenir llenguatge usuari
        userId = intent.getStringExtra("id").toString()
        loadLocale()

        // Control boton "Change Language"
        activitySettingsBinding!!.btnChangeLang.setOnClickListener {
            changeLanguage();
        }

        // Control boton "Support"
        findViewById<Button>(R.id.buttonHelp).setOnClickListener {
            val intent : Intent = Intent(this, HelpActivity::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        }
    }

    // Funcio per canviar de llenguatge
    private fun changeLanguage()
    {
    var languages = arrayOf("English", "EspaÃ±ol")
    var builder = AlertDialog.Builder(this)
    builder.setTitle("Choose Language");
    builder.setSingleChoiceItems(languages, -1) { dialogInterface, i->
        var menu = findViewById<TextView>(R.id.LangMenu)
        menu.text = languages[i]
        dialogInterface.dismiss()
        if(i==0)
        {
            setLocale("en")
            recreate()
        }
        if(i==1)
        {
            setLocale("es")
            recreate()
        }

    }
    builder.setNeutralButton("Cancel") { dialog, which ->
        dialog.cancel()
    }
    val mDialog = builder.create()
    mDialog.show()
}

    // Funcio per establir llenguatge local per defecte
    private fun setLocale(s: String)
    {
        var locale = Locale(s)
        Locale.setDefault(locale)

        var configuration = Configuration()
        configuration.locale = locale
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)

        var editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("app_lang", s)
        editor.apply()
    }

    // Funcio per carregar llenguatge local per defecte
    private fun loadLocale()
    {
        var preferences = getSharedPreferences("Settings", MODE_PRIVATE)
        var lang = preferences.getString("app_lang", "")
        if (lang != null) {
            setLocale(lang)
        }
    }
}