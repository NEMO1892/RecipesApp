package com.example.recipesapp.ui.chosen_recipe

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recipesapp.PdfDocumentAdapter
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentChosenRecipeBinding
import com.example.recipesapp.di.MyApplication
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.util.loadUrlImage
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import javax.inject.Inject

class ChosenRecipeFragment : Fragment() {

    private var binding: FragmentChosenRecipeBinding? = null

    private lateinit var viewModel: ChosenRecipeViewModel

    @Inject
    lateinit var viewModelProvider: ChosenRecipeViewModelProvider

    private var proteins: Float = 0f

    private var fats: Float = 0f

    private var carbs: Float = 0f

    private val args: ChosenRecipeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChosenRecipeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
        viewModel =
            ViewModelProvider(this, viewModelProvider)[ChosenRecipeViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            viewModel.oneRecipe.observe(viewLifecycleOwner) { hits ->
                checkHeart(hits.recipe)
                saveToFavoritesImageButton.setOnClickListener {
                    if (viewModel.isRecipeAdded((hits.recipe))) {
                        saveToFavoritesImageButton.setImageResource(R.drawable.ic_empty_heart)
                        viewModel.deleteRecipe(hits.recipe)
                    } else {
                        saveToFavoritesImageButton.setImageResource(R.drawable.ic_full_heart)
                        viewModel.addRecipe(hits.recipe)
                    }
                }
                labelTextView.text = hits.recipe.label
                foodImageView.loadUrlImage(hits.recipe.image)
                caloriesTextView.text =
                    (hits.recipe.calories / hits.recipe.yield).toInt().toString() + " kcal"
                countServingsTextView.text = hits.recipe.yield.toString() + " serve"
                countIngredientsTextView.text =
                    hits.recipe.ingredients?.size.toString() + " ingredients"
                hits.recipe.ingredientLines?.forEach {
                    ingredientsTextView.append(it + "\n")
                }
                hits.recipe.dietLabels?.forEach {
                    dietTextView.append("$it, ")
                }
                hits.recipe.healthLabels?.forEach {
                    healthTextView.append("$it, ")
                }
                cuisineTypeTextView.text = hits.recipe.cuisineType.firstOrNull()
                mealTypeTextView.text = hits.recipe.mealType?.firstOrNull()
                dishTypeTextView.text = hits.recipe.dishType?.firstOrNull()
                calculatePercents(
                    hits.recipe.totalNutrients.PROCNT.quantity,
                    hits.recipe.totalNutrients.FAT.quantity,
                    hits.recipe.totalNutrients.CHOCDF.quantity
                )
                setUpPieChart()
                loadDataToPieChart()

                floatingShareButton.setOnClickListener {
                    shareContacts("https://artyom.matveev/recipe/${getRecipeId()}")
//                    val printManager =
//                        requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
//                    val printAdapter = PdfDocumentAdapter(requireContext(), "rective")
//                    printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
                }
            }

            backImageButton.setOnClickListener {
                onBackButtonPressed()
            }
            viewModel.getOneCharacter(getRecipeId())
        }
    }

    private fun onBackButtonPressed() {
        findNavController().popBackStack()
    }

    private fun getRecipeId(): String = args.idRecipe

    private fun checkHeart(recipe: Recipe) {
        if (viewModel.isRecipeAdded(recipe)) {
            binding?.saveToFavoritesImageButton?.setImageResource(R.drawable.ic_full_heart)
        } else {
            binding?.saveToFavoritesImageButton?.setImageResource(R.drawable.ic_empty_heart)
        }
    }

    private fun calculatePercents(protein: Double, fat: Double, carb: Double) {
        val totalWeight = protein + fat + carb
        proteins = (protein / totalWeight).toFloat()
        fats = (fat / totalWeight).toFloat()
        carbs = (carb / totalWeight).toFloat()
    }

    private fun setUpPieChart() {
        binding?.run {
            pieChart.isDrawHoleEnabled = true
            pieChart.setUsePercentValues(true)
            pieChart.setEntryLabelTextSize(12f)
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.centerText = "Nutrients"
            pieChart.setCenterTextSize(21f)
            pieChart.description.isEnabled = false
            pieChart.legend.isEnabled = false
        }
    }

    private fun loadDataToPieChart() {
        binding?.run {
            val list: ArrayList<PieEntry> = arrayListOf()
            list.add(PieEntry(proteins, "Proteins"))
            list.add(PieEntry(fats, "Fats"))
            list.add(PieEntry(carbs, "Carbs"))
            val colors: ArrayList<Int> = arrayListOf()
            for (color: Int in ColorTemplate.MATERIAL_COLORS) {
                colors.add(color)
            }
            val dataSet = PieDataSet(list, "Expense category")
            dataSet.colors = colors
            val data = PieData(dataSet)
            data.setDrawValues(true)
            data.setValueFormatter(PercentFormatter(pieChart))
            data.setValueTextSize(12f)
            data.setValueTextColor(Color.BLACK)
            pieChart.data = data
            pieChart.animateY(1800, Easing.EaseInOutQuad)
        }
    }

    private fun shareContacts(label: String) {
        startActivity(
            Intent(
                Intent.ACTION_SEND
            ).apply {
                putExtra(Intent.EXTRA_TEXT, label)
                type = "text/*"
            })
    }
}