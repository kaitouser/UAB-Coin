package uab.uab_coin.activities

import android.os.Bundle

import uab.uab_coin.R
import uab.uab_coin.databinding.ActivityGetCoinsBinding


class GetCoinsActivity : DrawerBaseActivity()
{
    var activityGetCoinsBinding: ActivityGetCoinsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityGetCoinsBinding = ActivityGetCoinsBinding.inflate(layoutInflater)
        setContentView(activityGetCoinsBinding!!.root)
    }
}
