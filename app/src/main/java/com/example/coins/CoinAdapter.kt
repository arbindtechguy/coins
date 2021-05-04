package com.example.coins

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.coins.data.DBHandler
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import kotlinx.android.synthetic.main.coins_standard_item.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * CoinAdapter
 *  Adapter of coin object using with RecylerView
 */
class CoinAdapter(
        val coins: ArrayList<Coin>,
        val activity: Activity,
        val db: DBHandler,
        val coinSymbol: String
)
    : RecyclerView.Adapter<CoinViewHolder>() {

    /** type of view */
    companion object{
        const val STANDARD_VIEW = 0
        const val DIFFERENT_VIEW = 1
        private var mListener: OnItemClickListener? = null
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onFavClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        /* assign item view according to view type */
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.coins_standard_item,
                parent,
                false)
        return CoinViewHolder(itemView, db, coinSymbol)
    }

    override fun getItemCount(): Int {
        return coins.count()
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        holder.bind(coins[position], activity, coinSymbol)

    }

    override fun getItemViewType(position: Int): Int {
        return STANDARD_VIEW
    }
}

/**
 * CoinViewHolder
 *  holder of item view using with RecylerView
 *
 */
class CoinViewHolder(itemView: View, db: DBHandler, coinSymbol: String)
    : RecyclerView.ViewHolder(itemView) {

    /** coin name text view */
    val symbolItemView = itemView.itemSymbolTextView
    val changeItemView = itemView.itemChangeTextView
    val priceItemView = itemView.priceTextView
    val nameItemView = itemView.itemNameTextView
    val iconItemView = itemView.itemIconImageView
    val starOff = itemView.favIconStarOffImageView
    val starOn = itemView.favIconStarOnImageView2
    val dbHandler  = db

    val favCoins = dbHandler.readSymbols()
        /**
     * bind data from coin object with item view
     */
    fun bind(coin: Coin, activity: Activity, coinSymbol: String){
        /* assign coin name to nameItemView */

        symbolItemView.text = coin.symbol
        nameItemView.text = coin.name
        priceItemView.text = coin.price
        changeItemView.text = coin.change + "%"
        if (coin.change.toDouble() > 0) {
            changeItemView.text = "+" + coin.change + "%"
            changeItemView.setTextColor(Color.parseColor("#228B22"))

        }
        if (favCoins.contains(coin.symbol.toString())) {
            starOn.visibility = View.VISIBLE
//            Log.d("facCoins", itemView.context.resources.getResourceEntryName(favIcon.drawable.))
        }
        else {
            starOff.visibility = View.VISIBLE
        }

        val formatter: NumberFormat = DecimalFormat("#,###.####")
        priceItemView.text = "$" + (formatter.format(coin.price.toDouble()))
        if (coinSymbol == "jpy") {
            priceItemView.text = "￥" + (formatter.format(coin.price.toDouble()))
        }
        priceItemView.setTypeface(null, Typeface.NORMAL)

        itemView.findViewById<View>(R.id.favIconStarOffImageView).setOnClickListener(View.OnClickListener {
            Toast.makeText(activity, "Added " + coin.name + " to favorite list.", Toast.LENGTH_SHORT).show();
            // toggle star buttons
            starOn.visibility = View.VISIBLE
            starOff.visibility = View.INVISIBLE
            dbHandler.addFavCoin(coin)
        })
        itemView.findViewById<View>(R.id.favIconStarOnImageView2).setOnClickListener(View.OnClickListener {
            Toast.makeText(activity, "Removed " + coin.name + " from the favorite list.", Toast.LENGTH_SHORT).show();
            // toggle star buttons
            starOn.visibility = View.INVISIBLE
            starOff.visibility = View.VISIBLE
            dbHandler.removeFavCoin(coin.symbol)
            Log.d("Click", favCoins.toString());
        })
            /* load icon image to iconItemView */
        GlideToVectorYou
            .init()
            .with(activity)
            .setPlaceHolder(R.drawable.ic_launcher_background, R.mipmap.ic_launcher)
            .load(Uri.parse(coin.iconUrl), iconItemView)
    }


}