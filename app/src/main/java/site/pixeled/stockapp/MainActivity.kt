package site.pixeled.stockapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {
    private var mTimer = Timer()
    private var mCurrentSymbol: String? = null
    private var mCurrentStockPrice: Double? = null
    private var mIsMonitoring = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onMonitorClick(view: View) {
        if (!mIsMonitoring) {
            mIsMonitoring = true
            findViewById<Button>(R.id.monitorButton).text = "Cancel"
            val symbolText = findViewById<EditText>(R.id.stockSymbolField).text.toString()
            mCurrentSymbol = symbolText
            mTimer = Timer()
            mTimer.scheduleAtFixedRate(0, 1000) {
                WebClient.fetchStockPrice(this@MainActivity, symbolText).thenAccept { res ->
                    Log.i("StockStatus", res.stockPrice.toString())
                    val df = DecimalFormat("##.##")
                    findViewById<TextView>(R.id.stockPriceTextView).text =
                        "$mCurrentSymbol: ${df.format(res.stockPrice)}"
                    mCurrentStockPrice?.let {
                        if (res.stockPrice > it) {
                            findViewById<TextView>(R.id.stockPriceTextView).setTextColor(Color.GREEN)
                        } else {
                            findViewById<TextView>(R.id.stockPriceTextView).setTextColor(Color.RED)
                        }
                    }
                    mCurrentStockPrice = res.stockPrice
                }.exceptionally { err ->
                    Log.e("StockStatus", err.message.toString())
                    null
                }
            }
        } else {
            findViewById<Button>(R.id.monitorButton).text = "Monitor"
            mTimer.cancel()
            mIsMonitoring = false
        }


    }
}