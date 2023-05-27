package uab.uab_coin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator

import uab.uab_coin.databinding.ActivityGetCoinsBinding

class GetCoinsActivity : DrawerBaseActivity()
{
    companion object {
        const val RESULT = "RESULT"
    }
    private fun initScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        //integrator.setPrompt("Sigue aprendiendo en CursoKotlin.com")
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "El valor escaneado es: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private lateinit var binding: ActivityGetCoinsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetCoinsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnScan.setOnClickListener { initScanner() }


    }
}
