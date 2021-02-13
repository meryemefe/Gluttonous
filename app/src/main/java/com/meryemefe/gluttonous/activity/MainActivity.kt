package com.meryemefe.gluttonous.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.meryemefe.gluttonous.R
import com.meryemefe.gluttonous.fragment.FragmentFoodBasket
import com.meryemefe.gluttonous.fragment.FragmentFoodList
import com.meryemefe.gluttonous.fragment.FragmentHome
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // Define temporary fragment
    private lateinit var tempFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the initial fragment as FragmentHome
        supportFragmentManager.beginTransaction().add(R.id.fragment_holder, FragmentHome()).commit()
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        // Set title / subtitle of the action bar and fragment holder according to user's action
        bottom_nav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_go_home -> {
                    toolbar.title = getString(R.string.app_name)
                    toolbar.subtitle = ""
                    setSupportActionBar(toolbar)
                    tempFragment = FragmentHome()
                }
                R.id.action_list_food -> {
                    toolbar.title = getString(R.string.app_name)
                    toolbar.subtitle = getString(R.string.list_food_fragment)
                    setSupportActionBar(toolbar)
                    tempFragment = FragmentFoodList()
                }
                R.id.action_see_basket -> {
                    toolbar.title = getString(R.string.app_name)
                    toolbar.subtitle = getString(R.string.see_basket_fragment)
                    setSupportActionBar(toolbar)
                    tempFragment = FragmentFoodBasket()
                }
            }

            // Replace new fragment with the previous one
            supportFragmentManager.beginTransaction().replace(R.id.fragment_holder, tempFragment).commit()

            true
        }

    }
}
