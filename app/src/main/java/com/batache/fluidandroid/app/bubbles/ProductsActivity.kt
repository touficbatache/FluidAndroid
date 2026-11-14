package com.batache.fluidandroid.app.bubbles

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.batache.fluidandroid.R
import com.batache.fluidandroid.dp
import kotlinx.android.synthetic.main.activity_bubbles.*

class ProductsActivity : AppCompatActivity() {

//  private lateinit var bubblesManager: BubblesManager
//
//  private val colors = arrayOf("#FF9AA2", "#C7CEEA", "#B5EAD7", "#E2F0CB")
//
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_bubbles)
//
//    bubblesManager = BubblesManager(
//      context = this,
//      containerView = container,
//      model = BubblesManager.BubbleModel(cornerRadius = CORNER_RADIUS),
//      data = (
//          Array(9) { BubblesManager.BubbleData("", "", "breakfast") } +
//          Array(9) { BubblesManager.BubbleData("", "", "fruits") } +
//          Array(9) { BubblesManager.BubbleData("", "", "vegetables") } +
//          Array(9) { BubblesManager.BubbleData("", "", "bakery") } +
//          Array(9) { BubblesManager.BubbleData("", "", "cheese") } +
//          Array(9) { BubblesManager.BubbleData("", "", "meat_and_fish") } +
//          Array(9) { BubblesManager.BubbleData("", "", "desserts") } +
//          Array(9) { BubblesManager.BubbleData("", "", "oil_sauces_and_spices") } +
//          Array(4) { BubblesManager.BubbleData("", "", "coffee") } +
//          Array(4) { BubblesManager.BubbleData("", "", "wine_and_champagne") } +
//          Array(4) { BubblesManager.BubbleData("", "", "beer") } +
//          Array(4) { BubblesManager.BubbleData("", "", "beverages") } +
//          Array(4) { BubblesManager.BubbleData("", "", "parapharmacy") } +
//          Array(4) { BubblesManager.BubbleData("", "", "home_care") } +
//          Array(4) { BubblesManager.BubbleData("", "", "pets") }
//      ).toList(),
//      categories = arrayListOf(
//        BubblesManager.CategoryData(
//          category = "breakfast",
//          title = "Breakfast",
//          backgroundColor = Color.parseColor(colors[0])
//        ),
//        BubblesManager.CategoryData(
//          category = "fruits",
//          title = "Fruits",
//          backgroundColor = Color.parseColor(colors[1])
//        ),
//        BubblesManager.CategoryData(
//          category = "vegetables",
//          title = "Vegetables",
//          backgroundColor = Color.parseColor(colors[2])
//        ),
//        BubblesManager.CategoryData(
//          category = "bakery",
//          title = "Bakery",
//          backgroundColor = Color.parseColor(colors[3])
//        ),
//        BubblesManager.CategoryData(
//          category = "cheese",
//          title = "Cheese",
//          backgroundColor = Color.parseColor(colors[0])
//        ),
//        BubblesManager.CategoryData(
//          category = "meat_and_fish",
//          title = "Meat & fish",
//          backgroundColor = Color.parseColor(colors[1])
//        ),
//        BubblesManager.CategoryData(
//          category = "desserts",
//          title = "Desserts",
//          backgroundColor = Color.parseColor(colors[2])
//        ),
//        BubblesManager.CategoryData(
//          category = "oil_sauces_and_spices",
//          title = "Oil, sauces and spices",
//          backgroundColor = Color.parseColor(colors[3])
//        ),
//        BubblesManager.CategoryData(
//          category = "coffee",
//          title = "Coffee",
//          backgroundColor = Color.parseColor(colors[0])
//        ),
//        BubblesManager.CategoryData(
//          category = "wine_and_champagne",
//          title = "Wine & champagne",
//          backgroundColor = Color.parseColor(colors[1])
//        ),
//        BubblesManager.CategoryData(
//          category = "beer",
//          title = "Beer",
//          backgroundColor = Color.parseColor(colors[2])
//        ),
//        BubblesManager.CategoryData(
//          category = "beverages",
//          title = "Beverages",
//          backgroundColor = Color.parseColor(colors[3])
//        ),
//        BubblesManager.CategoryData(
//          category = "parapharmacy",
//          title = "Parapharmacy",
//          backgroundColor = Color.parseColor(colors[0])
//        ),
//        BubblesManager.CategoryData(
//          category = "home_care",
//          title = "Home care",
//          backgroundColor = Color.parseColor(colors[1])
//        ),
//        BubblesManager.CategoryData(
//          category = "pets",
//          title = "Pets",
//          backgroundColor = Color.parseColor(colors[2])
//        )
//      )
//    )
//  }

  private lateinit var bubblesManager: BubblesManagerRefined

//  private val colors = arrayOf("#FF9AA2", "#FFB7B2", "#FFDAC1", "#E2F0CB", "#B5EAD7", "#C7CEEA")

  // https://flatuicolors.com/palette/ru
  private val colors = arrayOf("#f3a683", "#f7d794", "#778beb", "#e77f67", "#cf6a87", "#f19066", "#f5cd79", "#546de5", "#e15f41", "#c44569", "#786fa6", "#f8a5c2", "#63cdda", "#ea8685", "#596275")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bubbles)

    bubblesManager = BubblesManagerRefined(
      context = this,
      containerView = container,
      model = BubblesManagerRefined.BubbleModel(cornerRadius = CORNER_RADIUS),
      data = (
          Array(11) { BubblesManagerRefined.BubbleData("", "", "breakfast") } +
          Array(8) { BubblesManagerRefined.BubbleData("", "", "fruits") } +
          Array(7) { BubblesManagerRefined.BubbleData("", "", "vegetables") } +
          Array(13) { BubblesManagerRefined.BubbleData("", "", "bakery") } +
          Array(9) { BubblesManagerRefined.BubbleData("", "", "cheese") } +
          Array(9) { BubblesManagerRefined.BubbleData("", "", "meat_and_fish") } +
          Array(9) { BubblesManagerRefined.BubbleData("", "", "desserts") } +
          Array(9) { BubblesManagerRefined.BubbleData("", "", "oil_sauces_and_spices") } +
          Array(4) { BubblesManagerRefined.BubbleData("", "", "coffee") } +
          Array(4) { BubblesManagerRefined.BubbleData("", "", "wine_and_champagne") } +
          Array(4) { BubblesManagerRefined.BubbleData("", "", "beer") } +
          Array(4) { BubblesManagerRefined.BubbleData("", "", "beverages") } +
          Array(4) { BubblesManagerRefined.BubbleData("", "", "parapharmacy") } +
          Array(4) { BubblesManagerRefined.BubbleData("", "", "home_care") } +
          Array(4) { BubblesManagerRefined.BubbleData("", "", "pets") }
      ).toList(),
      categories = arrayListOf(
        BubblesManagerRefined.CategoryData(
          category = "breakfast",
          title = "Breakfast",
          backgroundColor = Color.parseColor(colors[0])
        ),
        BubblesManagerRefined.CategoryData(
          category = "fruits",
          title = "Fruits",
          backgroundColor = Color.parseColor(colors[1])
        ),
        BubblesManagerRefined.CategoryData(
          category = "vegetables",
          title = "Vegetables",
          backgroundColor = Color.parseColor(colors[2])
        ),
        BubblesManagerRefined.CategoryData(
          category = "bakery",
          title = "Bakery",
          backgroundColor = Color.parseColor(colors[3])
        ),
        BubblesManagerRefined.CategoryData(
          category = "cheese",
          title = "Cheese",
          backgroundColor = Color.parseColor(colors[4])
        ),
        BubblesManagerRefined.CategoryData(
          category = "meat_and_fish",
          title = "Meat & fish",
          backgroundColor = Color.parseColor(colors[5])
        ),
        BubblesManagerRefined.CategoryData(
          category = "desserts",
          title = "Desserts",
          backgroundColor = Color.parseColor(colors[6])
        ),
        BubblesManagerRefined.CategoryData(
          category = "oil_sauces_and_spices",
          title = "Oil, sauces and spices",
          backgroundColor = Color.parseColor(colors[7])
        ),
        BubblesManagerRefined.CategoryData(
          category = "coffee",
          title = "Coffee",
          backgroundColor = Color.parseColor(colors[8])
        ),
        BubblesManagerRefined.CategoryData(
          category = "wine_and_champagne",
          title = "Wine & champagne",
          backgroundColor = Color.parseColor(colors[9])
        ),
        BubblesManagerRefined.CategoryData(
          category = "beer",
          title = "Beer",
          backgroundColor = Color.parseColor(colors[10])
        ),
        BubblesManagerRefined.CategoryData(
          category = "beverages",
          title = "Beverages",
          backgroundColor = Color.parseColor(colors[11])
        ),
        BubblesManagerRefined.CategoryData(
          category = "parapharmacy",
          title = "Parapharmacy",
          backgroundColor = Color.parseColor(colors[12])
        ),
        BubblesManagerRefined.CategoryData(
          category = "home_care",
          title = "Home care",
          backgroundColor = Color.parseColor(colors[13])
        ),
        BubblesManagerRefined.CategoryData(
          category = "pets",
          title = "Pets",
          backgroundColor = Color.parseColor(colors[14])
        )
      )
    )
  }

  companion object {
    private val CORNER_RADIUS = 25f.dp
  }

}