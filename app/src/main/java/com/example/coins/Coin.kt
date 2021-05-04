package com.example.coins

/**
 * Coin.kt
 *  Object that containg obtained coin data from json
 *
 *  name : name of coin
 *  description : description of coin
 *  iconUrl : url of coin icon
 *  bDiffView : if true, coin view will be displayed as different view
 */
data class Coin(
    val name: String,
    val iconUrl: String,
    val price: String,
    val symbol: String,
    val change: String,
)