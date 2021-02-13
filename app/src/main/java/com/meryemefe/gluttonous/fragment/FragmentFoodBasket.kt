package com.meryemefe.gluttonous.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.meryemefe.gluttonous.R
import com.meryemefe.gluttonous.adapter.FoodBasketCardAdapter
import com.meryemefe.gluttonous.entity.FoodOrder
import kotlinx.android.synthetic.main.fragment_food_basket.*
import kotlinx.android.synthetic.main.fragment_food_basket.view.*
import org.json.JSONObject

class FragmentFoodBasket  : Fragment(),  SearchView.OnQueryTextListener {

    // Define food list and food basket card adapter
    private lateinit var foodBasketList: ArrayList<FoodOrder>
    private lateinit var adapter: FoodBasketCardAdapter

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_food_basket, container, false)

        // Set the RecyclerView
        view.rvFoodBasket.setHasFixedSize(true)
        view.rvFoodBasket.layoutManager = LinearLayoutManager(view.context)

        // Get current food list in the basket
        getFoodBasket()

        return view
    }

    /**
     * Activate options menu on onCreate method
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Activate SearchView structure
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // Inflate toolbar_search menu
        inflater.inflate(R.menu.toolbar_search_menu, menu)

        // Define search item as SearchView
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView

        // Set the listener of searchView
        searchView.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchFood(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        // The fragment was replaced, so ignore
        if (!isVisible){
            return true
        }

        // If input is not 'change fragment', search food
        searchFood(newText)
        return true
    }

    /**
     * This method gets all food in the basket and updates recycler view.
     */
    private fun getFoodBasket(){
        val url = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val request = StringRequest(Request.Method.GET,url, { response ->

            foodBasketList = ArrayList()
            try {
                val jsonObject = JSONObject(response)
                val curFoodList = jsonObject.getJSONArray("sepet_yemekler")

                for (i in 0 until curFoodList.length()){
                    val f = curFoodList.getJSONObject(i)

                    val food = FoodOrder( f.getInt("yemek_id"),
                        f.getString("yemek_adi"),
                        f.getString("yemek_resim_adi"),
                        f.getInt("yemek_fiyat"),
                        f.getInt("yemek_siparis_adet"))
                    foodBasketList.add(food)
                }
            } catch (e: Exception){
                //e.printStackTrace()
            }
            adapter = FoodBasketCardAdapter(this.context as Context, foodBasketList)
            rvFoodBasket.adapter = adapter
        }, { error ->
            error.printStackTrace()
        })

        Volley.newRequestQueue(this.context).add(request)
    }

    /**
     * This method searches food according to search word.
     * @param searchWord: String
     */
    private fun searchFood(searchWord: String) {
        val url = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val request = StringRequest(Request.Method.GET,url, { response ->

            foodBasketList.clear()
            try {
                val jsonObject = JSONObject(response)
                val curFoodList = jsonObject.getJSONArray("sepet_yemekler")

                for (i in 0 until curFoodList.length()){
                    val f = curFoodList.getJSONObject(i)

                    if (f.getString("yemek_adi").contains(searchWord, true)){
                        val food = FoodOrder( f.getInt("yemek_id"),
                                f.getString("yemek_adi"),
                                f.getString("yemek_resim_adi"),
                                f.getInt("yemek_fiyat"),
                                f.getInt("yemek_siparis_adet"))
                        foodBasketList.add(food)
                    }

                }
            } catch (e: Exception){
                //e.printStackTrace()
            }
            adapter.notifyDataSetChanged()
        }, { error ->
            error.printStackTrace()
        })

        Volley.newRequestQueue(this.context).add(request)
    }

}