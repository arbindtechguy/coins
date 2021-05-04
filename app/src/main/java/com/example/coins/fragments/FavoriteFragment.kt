@file:Suppress("DEPRECATION")
package com.example.coins.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.coins.Coin
import com.example.coins.CoinAdapter
import com.example.coins.FavCoinAdapter
import com.example.coins.R
import com.example.coins.data.DBHandler
import kotlinx.android.synthetic.main.coins_standard_item.view.*
import kotlinx.android.synthetic.main.fragment_exchange.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Boolean.TRUE
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val coins = ArrayList<Coin>()
    private lateinit var adapter: FavCoinAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_favorite, container, false)
        /* add divider decoration to RecyclerView */
        //recycler
        val favRecyclerView: RecyclerView = view.findViewById(R.id.favRecyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        favRecyclerView.layoutManager = layoutManager

        /* create coin adapter then assign to MainActivity's adapter and RecyclerView's adapter */
        var db = context?.let { DBHandler(it) }
        adapter = db?.let { FavCoinAdapter(coins, requireActivity(), it, "usd") }!!
        favRecyclerView.adapter = adapter

        val favMainSwipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.favMainSwipeRefreshLayout)

        view.findViewById<View>(R.id.radioCurrencyJpy).setOnClickListener(View.OnClickListener {
            coins.clear()
            adapter = FavCoinAdapter(coins, requireActivity(), db, "jpy")
            favRecyclerView.adapter = adapter
            CoinRankingLoaderTask().execute("jpy")
            favMainSwipeRefreshLayout.isRefreshing = false
        })
        view.findViewById<View>(R.id.radioCurrencyUSD).setOnClickListener(View.OnClickListener {
            coins.clear()
            adapter = FavCoinAdapter(coins, requireActivity(), db, "usd")
            favRecyclerView.adapter = adapter
            CoinRankingLoaderTask().execute("usd")
            favMainSwipeRefreshLayout.isRefreshing = false
        })

        CoinRankingLoaderTask().execute()

        /* set action to refresh when pull the screen */
        favMainSwipeRefreshLayout.setOnRefreshListener {
            view.findViewById<View>(R.id.radioCurrencyUSD).isEnabled = true
            coins.clear()
            adapter = FavCoinAdapter(coins, requireActivity(), db, "usd")
            favRecyclerView.adapter = adapter
            CoinRankingLoaderTask().execute()
            favMainSwipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    internal inner class CoinRankingLoaderTask() : AsyncTask<String, String, String>()
    {
        override fun doInBackground(vararg params: String?): String {
            var tmp_url = "https://api.coinranking.com/v1/public/coins"
            if (params.isNotEmpty()) {
                tmp_url += "?base=${params[0]}"
            }
            val url = URL(tmp_url)

            return try {

                val json = url.readText()
                json
            } catch (e: IOException) {
                ""
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                if (result != "") {
                    updateCoins(result)
                }
            }
        }
    }
    fun updateCoins(json: String) {
        /* get JSON object from data */
        val jsonObj = JSONObject(json)
        var db = context?.let { DBHandler(it) }
        var favCoins = db?.readSymbols()

        /* get values object of data key from JSON object and collect in dataObj */
        val dataObject = jsonObj.getJSONObject("data")

        /* get values array of coins key from dataObj and collect in coinsArray*/
        val coinsArray = dataObject.getJSONArray("coins")

        /* clear old values from coins */
        coins.clear()

        /* iterator all object in coinsArray */
        for (coinId in 0 until coinsArray.length()) {
            /* get coin data from coinsArray */
            val coinObj = coinsArray.getJSONObject(coinId)
            /* every 5 coins view display with different view */

            /* create coin instance with above data */
            val coin = Coin(
                    coinObj.getString("name"),
                    coinObj.getString("iconUrl"),
                    coinObj.getString("price"),
                    coinObj.getString("symbol"),
                    coinObj.getString("change"),
            )

            /* collect coin instance in coins collection */
            if (favCoins!!.contains(coin.symbol.toString())) {
                coins.add(coin)
            }
        }

        /* notify to adapter */
        adapter.notifyDataSetChanged()
    }
}