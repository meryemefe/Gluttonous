package com.meryemefe.gluttonous.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.meryemefe.gluttonous.R
import com.meryemefe.gluttonous.adapter.FoodCardAdapter
import com.meryemefe.gluttonous.entity.Food
import kotlinx.android.synthetic.main.fragment_food_list.*
import kotlinx.android.synthetic.main.fragment_food_list.view.*
import org.json.JSONObject

class FragmentFoodList : Fragment(), SearchView.OnQueryTextListener {

    // Define food list and food card adapter
    private lateinit var foodList: ArrayList<Food>
    private lateinit var adapter: FoodCardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_food_list, container, false)

        // Set the RecyclerView
        view.rvFoodList.setHasFixedSize(true)
        view.rvFoodList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Get current food list
        getAllFood()

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
     * This method gets all food and updates recycler view.
     */
    private fun getAllFood(){
        val url = "http://kasimadalan.pe.hu/yemekler/tum_yemekler.php"

        val request = StringRequest(Request.Method.GET, url, { response ->

            foodList = ArrayList()
            try {
                val jsonObject = JSONObject(response)
                val curFoodList = jsonObject.getJSONArray("yemekler")

                for (i in 0 until curFoodList.length()) {
                    val f = curFoodList.getJSONObject(i)

                    val food = Food(
                            f.getInt("yemek_id"),
                            f.getString("yemek_adi"),
                            f.getString("yemek_resim_adi"),
                            f.getInt("yemek_fiyat")
                    )
                    foodList.add(food)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            adapter = FoodCardAdapter(this.context as Context, foodList)
            rvFoodList.adapter = adapter
        }, {
        })

        Volley.newRequestQueue(this.context).add(request)
    }

    /**
     * This method searches food according to search word.
     * @param searchWord: String
     */
    private fun searchFood(searchWord: String){

        val url = "http://kasimadalan.pe.hu/yemekler/tum_yemekler_arama.php"

        val request = object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    foodList.clear()
                    try {
                        val jsonObject = JSONObject(response)
                        val curFoodList = jsonObject.getJSONArray("yemekler")

                        for (i in 0 until curFoodList.length()) {
                            val f = curFoodList.getJSONObject(i)

                            val food = Food(
                                    f.getInt("yemek_id"),
                                    f.getString("yemek_adi"),
                                    f.getString("yemek_resim_adi"),
                                    f.getInt("yemek_fiyat")
                            )
                            foodList.add(food)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    adapter.notifyDataSetChanged()
                },
                Response.ErrorListener {
                }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["yemek_adi"] = searchWord
                return params
            }
        }

        Volley.newRequestQueue(this.context).add(request)
    }

}