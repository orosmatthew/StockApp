package site.pixeled.stockapp

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import site.pixeled.stockapp.models.StockStatus
import java.util.concurrent.CompletableFuture

object WebClient {
    private var mQueue: RequestQueue? = null

    private fun init(context: Context) {
        mQueue = Volley.newRequestQueue(context);
    }

    fun fetchStockPrice(context: Context, symbol: String): CompletableFuture<StockStatus> {
        if (mQueue == null) {
            init(context)
        }
        val future = CompletableFuture<StockStatus>()
        val request = JsonObjectRequest(
            Request.Method.GET,
            "https://mopsdev.bw.edu/~bkrupp/330/assignments/stock.php?stockSymbol=$symbol",
            null,
            { res ->
                val stockStatus = Gson().fromJson(res.toString(), StockStatus::class.java)
                future.complete(stockStatus)
            },
            { err ->
                future.completeExceptionally(err)
            }
        )
        mQueue?.add(request)
        return future
    }
}