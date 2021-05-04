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
 *
 *      created by Chanisa Phengphon
 */
class FavCoinAdapter(
        val coins: ArrayList<Coin>,
        val activity: Activity,
        val db: DBHandler,
        val coinSymbol: String
)
    : RecyclerView.Adapter<FavCoinViewHolder>() {
    val favCoins = db.readSymbols()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavCoinViewHolder {
        /* assign item view according to view type */
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.coins_standard_item,
                parent,
                false)
        return FavCoinViewHolder(itemView, db)
    }

    override fun getItemCount(): Int {
        return coins.count()
    }

    override fun onBindViewHolder(holder: FavCoinViewHolder, position: Int) {
        holder.bind(coins[position], activity, coinSymbol)

        //removing from fav coins
        holder.starOn.setOnClickListener(View.OnClickListener {
            Toast.makeText(activity, "Removed " + coins[position].name + " from the favorite list.", Toast.LENGTH_SHORT).show();
            holder.dbHandler.removeFavCoin(coins[position].symbol)
            coins.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        })

    }

    override fun getItemViewType(position: Int): Int {
        return STANDARD_VIEW
    }
}

/**
 * FavCoinViewHolder
 *  holder of item view using with RecylerView
 *
 */
class FavCoinViewHolder(itemView: View, db: DBHandler)
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
        starOn.visibility = View.VISIBLE

        val formatter: NumberFormat = DecimalFormat("#,###.####")
        priceItemView.text = "$" + (formatter.format(coin.price.toDouble()))
        if (coinSymbol == "jpy") {
            priceItemView.text = "￥" + (formatter.format(coin.price.toDouble()))
        }
        priceItemView.setTypeface(null, Typeface.NORMAL)


        /* load icon image to iconItemView */
        GlideToVectorYou
                .init()
                .with(activity)
                .setPlaceHolder(R.drawable.ic_launcher_background, R.mipmap.ic_launcher)
                .load(Uri.parse(coin.iconUrl), iconItemView)
    }


}
