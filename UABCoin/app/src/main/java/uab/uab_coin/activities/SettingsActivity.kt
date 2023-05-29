@file:Suppress("DEPRECATION")

package uab.uab_coin.activities

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import uab.uab_coin.R
import uab.uab_coin.databinding.ActivitySettingsBinding
import java.util.Locale

class SettingsActivity  : DrawerBaseActivity()
{
    var activitySettingsBinding: ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding!!.root)
        loadLocale()
        activitySettingsBinding!!.btnChangeLang.setOnClickListener {
            changeLanguage();
        }
    }
    private fun changeLanguage() {
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

    private fun setLocale(s: String) {
        var locale = Locale(s)
        Locale.setDefault(locale)

        var configuration = Configuration()
        configuration.locale = locale
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)

        var editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("app_lang", s)
        editor.apply()
    }
    private fun loadLocale(){
        var preferences = getSharedPreferences("Settings", MODE_PRIVATE)
        var lang = preferences.getString("app_lang", "")
        if (lang != null) {
            setLocale(lang)
        }
    }
}