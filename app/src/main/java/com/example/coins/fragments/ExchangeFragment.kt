@file:Suppress("DEPRECATION")

package com.example.coins.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.coins.Coin
import com.example.coins.CoinAdapter
import com.example.coins.R
import com.example.coins.data.DBHandler
import kotlinx.android.synthetic.main.fragment_exchange.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExchangeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExchangeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val coins = ArrayList<Coin>()
    /** coin adapter object for RecyclerView */
    private lateinit var adapter: CoinAdapter

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
    ): View ? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_exchange, container, false)
        /* add divider decoration to RecyclerView */
        //recycler
        val mainRecyclerView: RecyclerView = view.findViewById(R.id.mainRecyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        mainRecyclerView.layoutManager = layoutManager

        /* create coin adapter then assign to MainActivity's adapter and RecyclerView's adapter */
        var db = context?.let { DBHandler(it) }

        adapter = db?.let { CoinAdapter(coins, requireActivity(), it, "usd")}!!
        mainRecyclerView.adapter = adapter


        view.findViewById<View>(R.id.radioCurrencyJpy).setOnClickListener(View.OnClickListener {
            coins.clear()
            adapter = CoinAdapter(coins, requireActivity(), db, "jpy")
            mainRecyclerView.adapter = adapter
            CoinRankingLoaderTask().execute("jpy")
            mainSwipeRefreshLayout.isRefreshing = false
        })
        view.findViewById<View>(R.id.radioCurrencyUSD).setOnClickListener(View.OnClickListener {
            coins.clear()
            adapter = CoinAdapter(coins, requireActivity(), db, "usd")
            mainRecyclerView.adapter = adapter
            CoinRankingLoaderTask().execute("usd")
            mainSwipeRefreshLayout.isRefreshing = false
        })

        CoinRankingLoaderTask().execute()


        /* set action to refresh when pull the screen */
        val mainSwipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.mainSwipeRefreshLayout)
        mainSwipeRefreshLayout.setOnRefreshListener {
            coins.clear()
            adapter = CoinAdapter(coins, requireActivity(), db, "usd")
            mainRecyclerView.adapter = adapter
            CoinRankingLoaderTask().execute()
            mainSwipeRefreshLayout.isRefreshing = false
        }
//        val favIcon = view.findViewById(R.id.favIconImageView)
//        favIcon.setOnClickListener(View.OnClickListener {
//            Toast.makeText(context, "clicked", Toast.LENGTH_LONG);
//        })
        mainRecyclerView.runWhenReady {
            view.findViewById<View>(R.id.loadingPanel).visibility = View.GONE
        }
        return view
    }
    private fun RecyclerView.runWhenReady(action: () -> Unit) {
        val globalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                action()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExchangeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExchangeFragment().apply {
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
            coins.add(coin)
        }

        /* notify to adapter */
        adapter.notifyDataSetChanged()
    }
}