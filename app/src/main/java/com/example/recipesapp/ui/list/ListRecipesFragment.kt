package com.example.recipesapp.ui.list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.recipesapp.R
import com.example.recipesapp.connectivity.ConnectivityLiveData
import com.example.recipesapp.databinding.FragmentListRecipesBinding
import com.example.recipesapp.di.MyApplication
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.model.RecipeLoadingState
import com.example.recipesapp.ui.filter_modal_bottom_sheet.FilterModalBottomSheet
import com.example.recipesapp.ui.list.adapter.ListRecipesAdapter
import com.example.recipesapp.ui.navigation.BottomNavigationFragmentDirections
import com.example.recipesapp.util.findTopNavController
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONException
import javax.inject.Inject


class ListRecipesFragment : Fragment() {

    private var mutableOnePage: String = ""

    private var binding: FragmentListRecipesBinding? = null

    private lateinit var viewModel: ListRecipesViewModel

    @Inject
    lateinit var viewModelProvider: ListRecipesViewModelProvider

    @Inject
    lateinit var connectivityLiveData: ConnectivityLiveData

    private val auth = FirebaseAuth.getInstance()

    private val database =
        Firebase.database("https://recipesapp-22212-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Users")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRecipesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(context, R.anim.from_top)
        } else {
            AnimationUtils.loadAnimation(context, R.anim.to_bottom)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
        viewModel =
            ViewModelProvider(this, viewModelProvider)[ListRecipesViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readFirebaseData()
        initialiseObservers()
        binding?.run {
            viewModel.run {
                idNestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        page.observe(viewLifecycleOwner) {
                            mutableOnePage = it
                        }
                        getDataFromAPI(mutableOnePage)
                    }
                })
                fetchRecipeByQuery(getRandomTypeOfDish())
                searchEditText.doAfterTextChanged {
                    onSearchQuery(it.toString())
                }
                filterImageButton.setOnClickListener {
                    val filterModalBottomSheet = FilterModalBottomSheet { query, diet, health ->
                        if (diet != "" && health != "") {
                            createChips(diet)
                            createChips(health)
                            fetchRecipeByQuery(query, diet, health)
                        } else if (diet != "") {
                            createChips(diet)
                            fetchRecipeByQuery(query, diet)
                        } else if (health != "") {
                            createChips(health)
                            fetchRecipeByQuery(query, health)
                        }
                    }
                    filterModalBottomSheet.show(
                        requireActivity().supportFragmentManager,
                        filterModalBottomSheet.tag
                    )
                }
            }
        }
    }

    private fun getDataFromAPI(url: String) {
        val listRecipes: ArrayList<Recipe> = arrayListOf()
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val hits = response.getJSONArray("hits")
                    val onePage =
                        response.getJSONObject("_links").getJSONObject("next").getString("href")
                    for (i in 0 until hits.length()) {
                        val parser = JsonParser()
                        val mJson =
                            parser.parse(hits.getJSONObject(i).getJSONObject("recipe").toString())
                        val gson = Gson()
                        val oneRecipe: Recipe = gson.fromJson(mJson, Recipe::class.java)
                        listRecipes.add(oneRecipe)
                    }
                    viewModel.page.postValue(onePage)
                    initAdapter(listRecipes, false)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) {
        }
        queue.add(jsonObjectRequest)
    }

    private fun createChips(name: String?) {
        val chip = Chip(requireContext())
        chip.apply {
            text = name
            isClickable = true
            isCheckable = true
            chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_empty_heart)
            isChipIconVisible = false
            isCloseIconVisible = true
            binding?.run {
                entryChips.addView(chip as? View)
                chip.setOnCloseIconClickListener {
                    entryChips.removeView(chip as? View)
                }
            }
        }
    }

    private fun initialiseObservers() {
        binding?.run {
            viewModel.run {
                listRecipes.observe(viewLifecycleOwner) {
                    initAdapter(it)
                }
                recipeLoadingStateLiveData.observe(viewLifecycleOwner) {
                    onRecipeLoadingStateChanged(it)
                }
                connectivityLiveData.observe(viewLifecycleOwner) { isAvailable ->
                    when (isAvailable) {
                        true -> {
                            statusButton.visibility = View.GONE
                            searchEditText.visibility = View.VISIBLE
                            filterImageButton.visibility = View.VISIBLE
                            recyclerView.visibility = View.VISIBLE
                        }
                        false -> {
                            statusButton.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            filterImageButton.visibility = View.GONE
                            searchEditText.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun readFirebaseData() {
        auth.uid?.let {
            database.child(it).get().addOnSuccessListener { data ->
                val name = data.child("profileName").value as String?
                binding?.titleTextView?.text = "Hello, $name"
            }
                .addOnFailureListener {
                    Snackbar.make(
                        requireView(),
                        R.string.something_went_wrong_please_try_again_later,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Ok") {}
                        .show()
                }
        }
    }

    private fun onRecipeLoadingStateChanged(state: RecipeLoadingState) {
        binding?.run {
            when (state) {
                RecipeLoadingState.LOADING -> {
                    statusButton.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    loadingProgressBar.visibility = View.VISIBLE
                }
                RecipeLoadingState.LOADED -> {
                    statusButton.visibility = View.GONE
                    searchEditText.visibility = View.VISIBLE
                    filterImageButton.visibility = View.VISIBLE
                    recyclerView.visibility = View.VISIBLE
                    loadingProgressBar.visibility = View.GONE
                }
                RecipeLoadingState.ERROR -> {
                    statusButton.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    loadingProgressBar.visibility = View.GONE
                }
                RecipeLoadingState.INVALID_API_KEY -> {
                    statusButton.visibility = View.VISIBLE
                    statusButton.setText(R.string.invalid_api_key)
                    recyclerView.visibility = View.GONE
                    loadingProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun initAdapter(list: ArrayList<Recipe>, flag: Boolean = true) {
        binding?.run {
            if (recyclerView.adapter == null) {
                recyclerView.adapter = ListRecipesAdapter { id: String ->
                    val action =
                        BottomNavigationFragmentDirections.actionBottomNavigationFragmentToChosenRecipeFragment(
                            id
                        )
                    findTopNavController().navigate(action)
                }
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
            if (flag) {
                (recyclerView.adapter as? ListRecipesAdapter)?.setList(list)
            } else {
                (recyclerView.adapter as? ListRecipesAdapter)?.addList(list)
            }
        }
    }
}