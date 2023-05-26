package uab.uab_coin.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import uab.uab_coin.databinding.ActivityGetCoinsBinding
import uab.uab_coin.databinding.ActivityMainBinding
import uab.uab_coin.R


class GetCoinsActivity : DrawerBaseActivity()
{
    companion object {
        const val RESULT = "RESULT"
    }

    private lateinit var binding: ActivityGetCoinsBinding
    var activityGetCoinsBinding: ActivityGetCoinsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityGetCoinsBinding = ActivityGetCoinsBinding.inflate(layoutInflater)
        setContentView(activityGetCoinsBinding!!.root)


        binding.btnScan.setOnClickListener {
            val intent = Intent(applicationContext, ScanActivity::class.java)
            startActivity(intent)
        }

        val result = intent.getStringExtra(RESULT)

        if (result != null) {
            if (result.contains("https://") || result.contains("http://")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                startActivity(intent)
            } else {
                binding.result.text = result.toString()
            }
        }
    }
}
