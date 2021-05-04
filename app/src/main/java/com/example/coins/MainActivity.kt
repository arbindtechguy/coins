package com.example.coins

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.coins.fragments.ExchangeFragment
import com.example.coins.fragments.FavoriteFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.newFixedThreadPoolContext

class MainActivity : AppCompatActivity() {
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val favFragment = FavoriteFragment();
        val exchangeFragment = ExchangeFragment();

        makeCurrentFragment(exchangeFragment);
        bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.exchangeFragment -> makeCurrentFragment(exchangeFragment)
                R.id.favoriteFragment -> makeCurrentFragment(favFragment)
            }
            true
        }
        bottomNavigationMenu.selectedItemId = R.id.exchangeFragment

    }

    private fun makeCurrentFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment, fragment)
            commit()
        }
    }



}


