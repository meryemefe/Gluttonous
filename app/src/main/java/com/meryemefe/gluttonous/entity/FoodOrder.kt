package com.meryemefe.gluttonous.entity

import java.io.Serializable

data class FoodOrder(var food_id: Int, var food_name: String, var food_image_name: String, var food_price: Int, var food_count: Int) : Serializable {
}