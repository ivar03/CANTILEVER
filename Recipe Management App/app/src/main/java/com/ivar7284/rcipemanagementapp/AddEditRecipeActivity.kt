package com.ivar7284.rcipemanagementapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ivar7284.rcipemanagementapp.models.Recipe
import kotlinx.coroutines.launch

class AddEditRecipeActivity : AppCompatActivity() {
    private lateinit var viewModel: RecipeViewModel
    private lateinit var titleEdit: EditText
    private lateinit var ingredientsEdit: EditText
    private lateinit var instructionsEdit: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var saveButton: Button

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            if (selectedImageUri != null) {
                selectedImagePath = selectedImageUri.toString()
                imageView.setImageURI(selectedImageUri)
            }
        }
    }


    private var selectedImagePath: String? = null
    private var recipeId: Long = 0
    private var isEditMode = false
    
    private val categories = listOf("Breakfast", "Lunch", "Dinner", "Desserts", "Snacks")
    
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_recipe)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initViews()
        setupViewModel()
        setupCategorySpinner()
        checkEditMode()
        setupClickListeners()
    }
    
    private fun initViews() {
        titleEdit = findViewById(R.id.titleEdit)
        ingredientsEdit = findViewById(R.id.ingredientsEdit)
        instructionsEdit = findViewById(R.id.instructionsEdit)
        categorySpinner = findViewById(R.id.categorySpinner)
        imageView = findViewById(R.id.imageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        saveButton = findViewById(R.id.saveButton)
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
    }
    
    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }
    
    private fun checkEditMode() {
        recipeId = intent.getLongExtra("recipe_id", 0)
        isEditMode = recipeId != 0L
        
        if (isEditMode) {
            title = "Edit Recipe"
            loadRecipeData()
        } else {
            title = "Add Recipe"
        }
    }
    
    private fun loadRecipeData() {
        lifecycleScope.launch {
            val recipe = viewModel.getRecipeById(recipeId)
            recipe?.let {
                titleEdit.setText(it.title)
                ingredientsEdit.setText(it.ingredients)
                instructionsEdit.setText(it.instructions)
                
                val categoryIndex = categories.indexOf(it.category)
                if (categoryIndex >= 0) {
                    categorySpinner.setSelection(categoryIndex)
                }
                
                selectedImagePath = it.imagePath
                if (!it.imagePath.isNullOrEmpty()) {
                    Glide.with(this@AddEditRecipeActivity)
                        .load(it.imagePath)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_food)
                        .into(imageView)
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        selectImageButton.setOnClickListener {
            openImagePicker()
        }


        saveButton.setOnClickListener {
            saveRecipe()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }


    private fun saveRecipe() {
        val title = titleEdit.text.toString().trim()
        val ingredients = ingredientsEdit.text.toString().trim()
        val instructions = instructionsEdit.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        
        if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val recipe = Recipe(
            id = if (isEditMode) recipeId else 0,
            title = title,
            ingredients = ingredients,
            instructions = instructions,
            category = category,
            imagePath = selectedImagePath
        )
        
        if (isEditMode) {
            viewModel.updateRecipe(recipe)
            Toast.makeText(this, "Recipe updated!", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insertRecipe(recipe)
            Toast.makeText(this, "Recipe added!", Toast.LENGTH_SHORT).show()
        }
        
        finish()
    }
}

