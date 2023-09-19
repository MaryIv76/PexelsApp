package com.ivanova.pexelsapp.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ivanova.pexelsapp.Model.PhotoApi
import com.ivanova.pexelsapp.R
import com.ivanova.pexelsapp.View.RecyclerViews.PhotoItemDecoration
import com.ivanova.pexelsapp.View.RecyclerViews.PhotosRecyclerViewAdapter
import com.ivanova.pexelsapp.View.RecyclerViews.TitleItemDecoration
import com.ivanova.pexelsapp.View.RecyclerViews.TitlesRecyclerViewAdapter
import com.ivanova.pexelsapp.ViewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val searchView = view.findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }
        })

        vm.loadTitles()

        val recViewTitles: RecyclerView = view.findViewById(R.id.recView_titles)
        recViewTitles.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        val recViewTitlesAdapter = TitlesRecyclerViewAdapter()
        recViewTitles.adapter = recViewTitlesAdapter
        val titleItemMargin = resources.getDimensionPixelOffset(R.dimen.title_item_margin)
        recViewTitles.addItemDecoration(TitleItemDecoration(titleItemMargin))

        vm.titlesLive.observe(this) { titles ->
            recViewTitlesAdapter.setTitles(titles)
        }

        val recViewPhotos: RecyclerView = view.findViewById(R.id.recView_photos)
        recViewPhotos.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recViewPhotos.adapter = PhotosRecyclerViewAdapter(
            requireContext(),
            arrayListOf(
                "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg",
                "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg",
                "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg",
                "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg",
                "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg"
            )
        )
        val photoItemBottomMargin =
            resources.getDimensionPixelOffset(R.dimen.photo_item_bottom_margin)
        val photoItemHorizontalMargin =
            resources.getDimensionPixelOffset(R.dimen.photo_item_horizontal_margin)
        recViewPhotos.addItemDecoration(
            PhotoItemDecoration(
                photoItemBottomMargin, photoItemHorizontalMargin
            )
        )

        return view
    }

}