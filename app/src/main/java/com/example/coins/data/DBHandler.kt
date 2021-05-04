package com.example.coins.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.coins.Coin

/**
 * Created by VickY on 2017-11-28.
 */

val DATABASE_NAME ="Crypto"
val TABLE_NAME="fav_coins"
val COL_ID = "id"
val COL_NAME = "name"
val COL_ICON = "icon"
val COL_PRICE = "price"
val COL_SYMBOL = "symbol"
val COL_CHANGE = "change"

class DBHandler(var context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,1){
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE " + TABLE_NAME +" (" +
                COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " VARCHAR(256)," +
                COL_ICON + " VARCHAR(256)," +
                COL_PRICE + " VARCHAR(256)," +
                COL_SYMBOL + " VARCHAR(256)," +
                COL_CHANGE + " VARCHAR(256))"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?,oldVersion: Int,newVersion: Int) {
        TODO("not implemented")
    }

    fun addFavCoin(coin : Coin){
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_NAME,coin.name)
        cv.put(COL_ICON,coin.iconUrl)
        cv.put(COL_PRICE,coin.price)
        cv.put(COL_SYMBOL,coin.symbol)
        cv.put(COL_CHANGE,coin.change)
        var result = db.insert(TABLE_NAME,null,cv)
        if(result == -1.toLong())
            Toast.makeText(context,"Failed updating favorite list, try again!",Toast.LENGTH_SHORT).show()
    }

    fun readData() : MutableList<Coin>{
        var list : MutableList<Coin> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from " + TABLE_NAME
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()){
            do {
                list.add(Coin(
                    result.getString(result.getColumnIndex(COL_NAME)),
                    result.getString(result.getColumnIndex(COL_ICON)),
                    result.getString(result.getColumnIndex(COL_PRICE)),
                    result.getString(result.getColumnIndex(COL_SYMBOL)),
                    result.getString(result.getColumnIndex(COL_CHANGE))
                ))
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun readSymbols() : MutableList<String>{
        var list : MutableList<String> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from " + TABLE_NAME
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()){
            do {
                list.add(
                    result.getString(result.getColumnIndex(COL_SYMBOL)),
                )
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun removeFavCoin(symbol: String){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_SYMBOL='$symbol'",null)
        db.close()
    }
}