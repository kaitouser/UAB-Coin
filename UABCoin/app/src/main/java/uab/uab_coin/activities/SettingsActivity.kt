package uab.uab_coin.activities

import android.os.Bundle
import uab.uab_coin.databinding.ActivitySettingsBinding

class SettingsActivity  : DrawerBaseActivity()
{
    var activitySettingsBinding: ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding!!.root)
    }
}