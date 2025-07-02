package com.ivar7284.rcipemanagementapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.os.Build
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ivar7284.rcipemanagementapp.models.Recipe
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var categorySpinner: Spinner
    private lateinit var fabAdd: FloatingActionButton

    private val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Desserts", "Snacks")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        initViews()
        setupViewModel()
        setupRecyclerView()
        setupSearchView()
        setupCategorySpinner()
        setupFab()
        populateSampleRecipes()
        observeRecipes()
    }

    private fun populateSampleRecipes() {
        viewModel.allRecipes.observe(this) { recipes ->
            if (recipes.isEmpty()) {
                val sampleRecipes = listOf(
                    // 🍳 Breakfast
                    Recipe(
                        title = "Fluffy Pancakes",
                        ingredients = "Flour, Eggs, Milk, Baking Powder, Sugar, Salt",
                        instructions = "1. Mix dry ingredients.\n2. Add wet ingredients.\n3. Cook on griddle until golden.",
                        category = "Breakfast"
                    ),
                    Recipe(
                        title = "Masala Omelette",
                        ingredients = "Eggs, Onion, Tomato, Green Chilies, Coriander, Spices",
                        instructions = "1. Beat eggs with chopped veggies and spices.\n2. Cook on medium flame until set.",
                        category = "Breakfast"
                    ),
                    Recipe(
                        title = "Poha",
                        ingredients = "Flattened rice, Onion, Peanuts, Mustard seeds, Curry leaves",
                        instructions = "1. Rinse poha.\n2. Temper spices.\n3. Add onion, poha, peanuts and cook for 5 min.",
                        category = "Breakfast"
                    ),
                    Recipe(
                        title = "Smoothie Bowl",
                        ingredients = "Banana, Berries, Yogurt, Granola, Honey",
                        instructions = "1. Blend fruits with yogurt.\n2. Pour into bowl.\n3. Top with granola and honey.",
                        category = "Breakfast"
                    ),
                    Recipe(
                        title = "Upma",
                        ingredients = "Semolina, Onion, Green chilies, Spices, Water",
                        instructions = "1. Roast semolina.\n2. Sauté spices and onion.\n3. Add water and semolina. Cook till fluffy.",
                        category = "Breakfast"
                    ),

                    // 🍛 Lunch
                    Recipe(
                        title = "Rajma Chawal",
                        ingredients = "Kidney beans, Rice, Onion, Tomato, Spices",
                        instructions = "1. Cook rajma with spices.\n2. Cook rice separately.\n3. Serve hot together.",
                        category = "Lunch"
                    ),
                    Recipe(
                        title = "Grilled Chicken Salad",
                        ingredients = "Chicken breast, Lettuce, Tomatoes, Cucumber, Dressing",
                        instructions = "1. Grill chicken.\n2. Toss veggies and chicken with dressing.",
                        category = "Lunch"
                    ),
                    Recipe(
                        title = "Paneer Butter Masala",
                        ingredients = "Paneer, Tomato, Cream, Spices",
                        instructions = "1. Cook tomato puree with spices.\n2. Add paneer and cream.\n3. Cook until thick.",
                        category = "Lunch"
                    ),
                    Recipe(
                        title = "Veg Pulao",
                        ingredients = "Rice, Mixed veggies, Spices",
                        instructions = "1. Sauté spices and veggies.\n2. Add rice and water.\n3. Cook until rice is fluffy.",
                        category = "Lunch"
                    ),
                    Recipe(
                        title = "Dal Tadka",
                        ingredients = "Lentils, Onion, Tomato, Spices",
                        instructions = "1. Cook dal.\n2. Prepare tadka with spices.\n3. Mix with dal and simmer.",
                        category = "Lunch"
                    ),

                    // 🍽️ Dinner
                    Recipe(
                        title = "Palak Paneer",
                        ingredients = "Spinach, Paneer, Onion, Tomato, Spices",
                        instructions = "1. Blanch spinach and blend.\n2. Cook with spices.\n3. Add paneer cubes.",
                        category = "Dinner"
                    ),
                    Recipe(
                        title = "Chicken Curry",
                        ingredients = "Chicken, Onion, Tomato, Spices",
                        instructions = "1. Marinate chicken.\n2. Cook onion, tomato, spices.\n3. Add chicken and cook till tender.",
                        category = "Dinner"
                    ),
                    Recipe(
                        title = "Veg Stir Fry",
                        ingredients = "Mixed vegetables, Soy sauce, Garlic, Spices",
                        instructions = "1. Stir fry vegetables with garlic and spices.\n2. Add soy sauce.\n3. Cook until crisp-tender.",
                        category = "Dinner"
                    ),
                    Recipe(
                        title = "Chole",
                        ingredients = "Chickpeas, Onion, Tomato, Spices",
                        instructions = "1. Soak and boil chickpeas.\n2. Cook onion, tomato, spices.\n3. Add chickpeas and simmer.",
                        category = "Dinner"
                    ),
                    Recipe(
                        title = "Khichdi",
                        ingredients = "Rice, Moong dal, Spices, Veggies (optional)",
                        instructions = "1. Wash rice and dal.\n2. Cook with spices and veggies.\n3. Serve warm.",
                        category = "Dinner"
                    ),

                    // 🍰 Desserts
                    Recipe(
                        title = "Chocolate Mug Cake",
                        ingredients = "Flour, Cocoa, Sugar, Milk, Oil",
                        instructions = "1. Mix all ingredients in a mug.\n2. Microwave for 2 minutes.",
                        category = "Desserts"
                    ),
                    Recipe(
                        title = "Gulab Jamun",
                        ingredients = "Milk powder, Flour, Sugar syrup",
                        instructions = "1. Make dough balls.\n2. Fry until golden.\n3. Dip in sugar syrup.",
                        category = "Desserts"
                    ),
                    Recipe(
                        title = "Rice Kheer",
                        ingredients = "Rice, Milk, Sugar, Cardamom, Nuts",
                        instructions = "1. Cook rice in milk.\n2. Add sugar, cardamom, and nuts.\n3. Cook until thick.",
                        category = "Desserts"
                    ),
                    Recipe(
                        title = "Fruit Custard",
                        ingredients = "Milk, Custard powder, Sugar, Mixed fruits",
                        instructions = "1. Cook milk with custard powder and sugar.\n2. Cool and add chopped fruits.",
                        category = "Desserts"
                    ),
                    Recipe(
                        title = "Besan Ladoo",
                        ingredients = "Besan, Ghee, Sugar, Cardamom",
                        instructions = "1. Roast besan in ghee.\n2. Add sugar and cardamom.\n3. Shape into ladoos.",
                        category = "Desserts"
                    ),

                    // 🍪 Snacks
                    Recipe(
                        title = "Samosa",
                        ingredients = "Flour, Potato, Peas, Spices",
                        instructions = "1. Make dough.\n2. Prepare potato filling.\n3. Shape and fry samosas.",
                        category = "Snacks"
                    ),
                    Recipe(
                        title = "Veg Sandwich",
                        ingredients = "Bread, Veggies, Butter, Chutney",
                        instructions = "1. Layer veggies and chutney on bread.\n2. Grill or serve fresh.",
                        category = "Snacks"
                    ),
                    Recipe(
                        title = "Bhel Puri",
                        ingredients = "Puffed rice, Onion, Tomato, Chutneys, Sev",
                        instructions = "1. Mix all ingredients.\n2. Serve immediately to keep crispy.",
                        category = "Snacks"
                    ),
                    Recipe(
                        title = "Masala Corn",
                        ingredients = "Sweet corn, Butter, Spices, Lemon",
                        instructions = "1. Boil corn.\n2. Mix with butter and spices.\n3. Squeeze lemon before serving.",
                        category = "Snacks"
                    ),
                    Recipe(
                        title = "Aloo Tikki",
                        ingredients = "Potato, Spices, Bread crumbs",
                        instructions = "1. Mash potatoes with spices.\n2. Shape into tikkis.\n3. Shallow fry until golden.",
                        category = "Snacks"
                    ),
                )

                sampleRecipes.forEach { recipe ->
                    viewModel.insertRecipe(recipe)
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        categorySpinner = findViewById(R.id.categorySpinner)
        fabAdd = findViewById(R.id.fabAdd)
        val searchAutoComplete = searchView.findViewById<androidx.appcompat.widget.SearchView.SearchAutoComplete>(
            androidx.appcompat.R.id.search_src_text
        )

        searchAutoComplete?.setBackgroundResource(R.drawable.transparent_bg)
        searchAutoComplete?.background = ContextCompat.getDrawable(this, R.drawable.transparent_bg)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(
            onRecipeClick = { recipe -> openRecipeDetail(recipe) },
            onFavoriteClick = { recipe -> viewModel.toggleFavorite(recipe) },
            onRatingChange = { recipe, rating -> viewModel.updateRating(recipe, rating) }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchRecipes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    observeAllRecipes()
                } else {
                    viewModel.searchRecipes(newText)
                }
                return true
            }
        })
    }

    private fun setupCategorySpinner() {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                if (selectedCategory == "All") {
                    observeAllRecipes()
                } else {
                    observeCategoryRecipes(selectedCategory)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupFab() {
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditRecipeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeRecipes() {
        observeAllRecipes()

        viewModel.searchResults.observe(this) { searchResults ->
            if (searchResults.isNotEmpty()) {
                adapter.updateRecipes(searchResults)
            }
        }
    }

    private fun observeAllRecipes() {
        viewModel.allRecipes.observe(this) { recipes ->
            adapter.updateRecipes(recipes)
        }
    }

    private fun observeCategoryRecipes(category: String) {
        viewModel.getRecipesByCategory(category).observe(this) { recipes ->
            adapter.updateRecipes(recipes)
        }
    }

    private fun openRecipeDetail(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailActivity::class.java)
        intent.putExtra("recipe_id", recipe.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                showFavoriteRecipes()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFavoriteRecipes() {
        viewModel.getFavoriteRecipes().observe(this) { favorites ->
            adapter.updateRecipes(favorites)
        }
    }
}