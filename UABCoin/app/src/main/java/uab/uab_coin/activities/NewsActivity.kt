package uab.uab_coin.activities

import android.os.Bundle
import uab.uab_coin.databinding.ActivityNewsBinding

class NewsActivity : DrawerBaseActivity()
{
    var activityNewsBinding: ActivityNewsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewsBinding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(activityNewsBinding!!.root)
    }
}