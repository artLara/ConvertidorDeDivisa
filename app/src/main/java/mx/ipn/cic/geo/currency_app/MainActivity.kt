package mx.ipn.cic.geo.currency_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import com.google.firebase.database.*
import com.google.gson.Gson
import mx.ipn.cic.geo.currency_app.databinding.ActivityMainBinding
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var request: Request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Se coloca como comentario, cambio por usar viewbinding.
        // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Invocar el método para equivalencia de monedas.
        getCurrencyData().start()

        binding.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                binding.textMonedaBase.text = "MXN: $progress"
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                val currentPeso: Float = binding.textMonedaBase.text.toString().split(" ").toTypedArray()[1].toFloat()
                binding.textMonedaEuro.text = String.format("EUR: %.2f", request.rates.EUR*currentPeso)
                binding.textMonedaDolar.text = String.format("USD: %.2f", request.rates.USD*currentPeso)
                binding.textMonedaLibra.text = String.format("GBP: %.2f", request.rates.GBP*currentPeso)
                binding.textMonedaZAR.text = String.format("ZAR: %.2f", request.rates.ZAR*currentPeso)
                binding.textMonedaCOP.text = String.format("COP: %.2f", request.rates.COP*currentPeso)
                binding.textMonedaGEL.text = String.format("GEL: %.2f", request.rates.GEL*currentPeso)

            }
        })

        //Conexion a FireBase
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val referenciaBD : DatabaseReference = database.getReference("app_divisas/actualizacion")
        binding.save.setOnClickListener{
            val fecha = binding.textUltimActualizacion.text.toString()
            val precio = binding.textMonedaBase.text.toString()
            referenciaBD.child("actualizacion").child(fecha).setValue(precio)
        }
    }

    private fun getCurrencyData(): Thread
    {
        return Thread {
            val url = URL("https://open.er-api.com/v6/latest/mxn")
            val connection = url.openConnection() as HttpsURLConnection

            Log.d("Resultado Petición: ", connection.responseCode.toString())

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                request = Gson().fromJson(inputStreamReader, Request::class.java)
                updateUI(request)
                inputStreamReader.close()
                inputSystem.close()
            }
            else {
                binding.textUltimActualizacion.text = "PROBLEMA EN CONEXIÓN"
            }
        }
    }

    private fun updateUI(request: Request)
    {
        runOnUiThread {
            kotlin.run {
                binding.textUltimActualizacion.text = request.time_last_update_utc
                binding.textMonedaEuro.text = String.format("EUR: %.2f", request.rates.EUR)
                binding.textMonedaDolar.text = String.format("USD: %.2f", request.rates.USD)
                binding.textMonedaLibra.text = String.format("GBP: %.2f", request.rates.GBP)
                binding.textMonedaZAR.text = String.format("ZAR: %.2f", request.rates.ZAR)
                binding.textMonedaCOP.text = String.format("COP: %.2f", request.rates.COP)
                binding.textMonedaGEL.text = String.format("GEL: %.2f", request.rates.GEL)
            }
        }
    }
}