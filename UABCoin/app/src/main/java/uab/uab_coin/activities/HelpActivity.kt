package uab.uab_coin.activities
import android.os.Bundle
import uab.uab_coin.databinding.ActivityHelpBinding


class HelpActivity : DrawerBaseActivity()
{
    var activityHelpBinding : ActivityHelpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Afegir barra d'eines
        super.onCreate(savedInstanceState)
        activityHelpBinding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(activityHelpBinding!!.root)
    }
}