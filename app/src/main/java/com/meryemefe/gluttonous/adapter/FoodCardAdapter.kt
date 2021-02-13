package com.meryemefe.gluttonous.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.meryemefe.gluttonous.R
import com.meryemefe.gluttonous.entity.Food
import com.meryemefe.gluttonous.entity.FoodOrder
import com.squareup.picasso.Picasso

class FoodCardAdapter(private val mContext: Context, private val foodList: List<Food>)
    : RecyclerView.Adapter<FoodCardAdapter.CardDesignHolder>() {

    inner class CardDesignHolder( design: View) : RecyclerView.ViewHolder(design){

        // Properties
        var foodCard: CardView
        var imageViewFood: ImageView
        var imageViewGiveOrder: ImageView
        var textViewFoodName: TextView
        var textViewFoodPrice: TextView

        // Constructor
        init {
            foodCard = design.findViewById(R.id.foodCard)
            imageViewFood = design.findViewById(R.id.imageViewFoodImage)
            imageViewGiveOrder = design.findViewById(R.id.imageViewGiveOrder)
            textViewFoodName = design.findViewById(R.id.textViewFoodName)
            textViewFoodPrice = design.findViewById(R.id.textViewFoodPrice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignHolder {

        val design = LayoutInflater.from(mContext).inflate(R.layout.food_card_design, parent,false)
        return CardDesignHolder(design)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CardDesignHolder, position: Int) {

        // Get the food in this position
        val food = foodList[position]

        // Display its name, price, and image
        getImage(holder.imageViewFood, food.food_image_name)
        holder.textViewFoodName.text = food.food_name
        holder.textViewFoodPrice.text = "₺ ${food.food_price}"

        // Set the popup option so that user can give order
        holder.imageViewGiveOrder.setOnClickListener {
            showPopupWindow(food)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
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
     * This method opens a popup window that user can give order from the food clicked.
     * @param food: Food
     */
    @SuppressLint("SetTextI18n")
    private fun showPopupWindow(food: Food) {

        // Initialize alert dialog
        val dialogBuilder = AlertDialog.Builder(mContext)

        // Inflate popup_food_order layout
        val inflater = mContext.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_food_order, null)

        // Set the name and price TextViews and ImageView of the food on the popup window design
        popupView.findViewById<TextView>(R.id.textViewFoodOrderName).text = food.food_name
        popupView.findViewById<TextView>(R.id.textViewFoodOrderPrice).text = "₺ ${food.food_price}"
        getImage(popupView.findViewById(R.id.imageViewFoodOrder), food.food_image_name)

        // Set the initialized popup window as view of alert dialog and show it
        dialogBuilder.setView(popupView)
        val dialog = dialogBuilder.create()
        dialog.show()

        // Define all the buttons on the popup window
        val buttonIncrease = popupView.findViewById<Button>(R.id.buttonIncrease)
        val buttonDecrease = popupView.findViewById<Button>(R.id.buttonDecrease)
        val buttonCount = popupView.findViewById<Button>(R.id.buttonFoodCount)
        val buttonGiveOrder = popupView.findViewById<Button>(R.id.buttonGiveOrder)
        val buttonTotalOrderPrice = popupView.findViewById<Button>(R.id.buttonTotalOrderPrice)

        // Initialize the total price as the price of 1 food
        buttonTotalOrderPrice.text = "₺ ${food.food_price}"

        // When clicked buttonIncrease, increase the count of order by 1
        buttonIncrease.setOnClickListener {
            buttonCount.text = (buttonCount.text.toString().toInt() + 1).toString()
            buttonTotalOrderPrice.text = "₺ ${buttonCount.text.toString().toInt() * food.food_price}"
        }

        // When clicked buttonDecrease, decrease the count of order by 1
        buttonDecrease.setOnClickListener {
            if (buttonCount.text.toString().toInt() > 1){
                buttonCount.text = (buttonCount.text.toString().toInt() - 1).toString()
                buttonTotalOrderPrice.text = "₺ ${buttonCount.text.toString().toInt() * food.food_price}"
            }
        }

        // When clicked buttonGiveOrder, give the order
        buttonGiveOrder.setOnClickListener {
            val count = buttonCount.text.toString().toInt()

            val foodOrder = FoodOrder(food.food_id, food.food_name, food.food_image_name, food.food_price, count)
            giveOrder(foodOrder)
            dialog.dismiss()
        }

        // When clicked the close icon, close the popup window
        popupView.findViewById<ImageView>(R.id.imageViewClose).setOnClickListener {
            dialog.dismiss()
        }

    }

    /**
     * This method gives the order.
     * @param foodOrder: FoodOrder
     */
    private fun giveOrder( foodOrder: FoodOrder){

        val url = "http://kasimadalan.pe.hu/yemekler/insert_sepet_yemek.php"

        val request = object : StringRequest(Method.POST, url, Response.Listener {
            Toast.makeText(mContext,"'${foodOrder.food_name}' ${mContext.getString(R.string.info_added_food_order)}", Toast.LENGTH_SHORT).show()
        }, Response.ErrorListener { error ->
            error.printStackTrace()
        }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["yemek_id"] = foodOrder.food_id.toString()
                params["yemek_adi"] = foodOrder.food_name
                params["yemek_resim_adi"] = foodOrder.food_image_name
                params["yemek_fiyat"] = foodOrder.food_price.toString()
                params["yemek_siparis_adet"] = foodOrder.food_count.toString()

                return params
            }
        }

        Volley.newRequestQueue(mContext).add(request)
    }

}