package uab.uab_coin.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uab.uab_coin.R
import uab.uab_coin.databinding.ActivityGetCoinsBinding
import uab.uab_coin.databinding.ActivityMainBinding


class GetCoinsActivity : AppCompatActivity() {
    companion object {
        const val RESULT = "RESULT"
    }
    private lateinit var binding: ActivityGetCoinsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetCoinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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