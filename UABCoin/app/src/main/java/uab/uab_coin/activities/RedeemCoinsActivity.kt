package uab.uab_coin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uab.uab_coin.R
import uab.uab_coin.databinding.ActivityRedeemCoinsBinding


class RedeemCoinsActivity : DrawerBaseActivity()
{
    var activityRedeemCoinsBinding: ActivityRedeemCoinsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRedeemCoinsBinding = ActivityRedeemCoinsBinding.inflate(layoutInflater)
        setContentView(activityRedeemCoinsBinding!!.root)
    }
}

