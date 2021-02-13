package com.meryemefe.gluttonous.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.meryemefe.gluttonous.R
import com.meryemefe.gluttonous.entity.FoodOrder
import com.squareup.picasso.Picasso
import org.json.JSONObject

class FoodBasketCardAdapter(private val mContext: Context, private var foodOrderList: List<FoodOrder>)
    : RecyclerView.Adapter<FoodBasketCardAdapter.CardDesignHolder>() {

    inner class CardDesignHolder( design: View) : RecyclerView.ViewHolder(design){

        // Properties
        var foodBasketCard: CardView
        var imageViewFoodBasket: ImageView
        var textViewFoodBasketName: TextView
        var textViewFoodBasketAmount: TextView
        var textViewFoodBasketTotalPrice: TextView
        var imageViewRemoveBasket: ImageView

        // Constructor
        init {
            foodBasketCard = design.findViewById(R.id.foodBasketCard)
            imageViewFoodBasket = design.findViewById(R.id.imageViewFoodBasketImage)
            textViewFoodBasketName = design.findViewById(R.id.textViewFoodBasketName)
            textViewFoodBasketAmount = design.findViewById(R.id.textViewFoodBasketAmount)
            textViewFoodBasketTotalPrice = design.findViewById(R.id.textViewFoodBasketTotalPrice)
            imageViewRemoveBasket = design.findViewById(R.id.imageViewRemoveBasket)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignHolder {

        val design = LayoutInflater.from(mContext).inflate(R.layout.food_basket_card_design, parent,false)
        return CardDesignHolder(design)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CardDesignHolder, position: Int) {

        // Get the food in this position
        val food = foodOrderList[position]

        // Display its name, count, price, and image
        getImage(holder.imageViewFoodBasket, food.food_image_name)
        holder.textViewFoodBasketName.text = food.food_name
        holder.textViewFoodBasketAmount.text = "# ${food.food_count}"
        holder.textViewFoodBasketTotalPrice.text = "₺ ${food.food_price * food.food_count}"

        // Set the popup option so that user can remove this item from the basket
        holder.imageViewRemoveBasket.setOnClickListener {
            askForDeleteOrder( food)
        }

    }

    override fun getItemCount(): Int {
        return foodOrderList.size
    }

    /**
     * This method gets the image from the database and puts it to the given ImageView.
     * @param imageView: ImageView
     * @param foodImageName: String
     */
    private fun getImage(imageView: ImageView, foodImageName: String){

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/$foodImageName"
        Picasso.get().load(url)
                .placeholder(R.drawable.load_icon)
                .error(R.drawable.error_icon)
                .into(imageView)
    }

    /**
     * This method asks user whether s/he is sure to delete the food order.
     * @param food: FoodOrder
     */
    private fun askForDeleteOrder(food: FoodOrder){

        val alert = AlertDialog.Builder(mContext)
        alert.setTitle(mContext.getString(R.string.ask_delete_food_order))
        alert.setPositiveButton(mContext.getString(R.string.approve)){ d, i ->
            removeFoodFromBasket(food)

            Toast.makeText(mContext, "'${food.food_name}' ${mContext.getString(R.string.info_removed_food_order)}", Toast.LENGTH_SHORT).show()

            getFoodBasket()
        }
        alert.setNegativeButton(mContext.getString(R.string.decline)){ d, i ->
            d.dismiss()
        }
        alert.create().show()
    }

    /**
     * This method removes the food order from the basket.
     * @param food: FoodOrder
     */
    private fun removeFoodFromBasket(food: FoodOrder){

        val url = "http://kasimadalan.pe.hu/yemekler/delete_sepet_yemek.php"

        val request = object : StringRequest(Method.POST, url, Response.Listener {
            getFoodBasket()
        }, Response.ErrorListener { error ->
            error.printStackTrace()
        }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["yemek_id"] = food.food_id.toString()
                return params
            }
        }

        Volley.newRequestQueue(mContext).add(request)
    }

    /**
     * This method gets all the food in the basket.
     */
    private fun getFoodBasket(){
        val url = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val request = StringRequest(Request.Method.GET,url, { response ->
            val tempList = ArrayList<FoodOrder>()
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
                    tempList.add(food)
                }
            } catch (e: Exception){
                //e.printStackTrace()
            }
            foodOrderList = tempList
            notifyDataSetChanged()
        }, { error ->
            error.printStackTrace()
        })

        Volley.newRequestQueue(mContext).add(request)
    }

}