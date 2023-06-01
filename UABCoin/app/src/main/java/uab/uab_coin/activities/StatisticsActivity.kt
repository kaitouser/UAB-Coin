package uab.uab_coin.activities

import android.os.Bundle
import uab.uab_coin.databinding.ActivityStatisticsBinding


class StatisticsActivity : DrawerBaseActivity()
{
    var activityStatisticsBinding: ActivityStatisticsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Afegir barra d'eines
        super.onCreate(savedInstanceState)
        activityStatisticsBinding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(activityStatisticsBinding!!.root)

        var userId = intent.getStringExtra("id").toString()


    }
}