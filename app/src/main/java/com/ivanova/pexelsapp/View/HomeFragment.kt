package com.ivanova.pexelsapp.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ivanova.pexelsapp.R
import com.ivanova.pexelsapp.View.RecyclerViews.PhotoItemDecoration
import com.ivanova.pexelsapp.View.RecyclerViews.PhotosRecyclerViewAdapter
import com.ivanova.pexelsapp.View.RecyclerViews.TitleItemDecoration
import com.ivanova.pexelsapp.View.RecyclerViews.TitlesRecyclerViewAdapter
import com.ivanova.pexelsapp.ViewModel.MainViewModel
import kotlinx.coroutines.*


class HomeFragment : Fragment() {

    private lateinit var vm: MainViewModel

    private lateinit var textChangedJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = GONE

        // TITLES
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
        vm.loadTitles()

        // PHOTOS
        val recViewPhotos: RecyclerView = view.findViewById(R.id.recView_photos)
        val photosLayoutManager: StaggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recViewPhotos.layoutManager = photosLayoutManager
        val recViewPhotosAdapter = PhotosRecyclerViewAdapter(
            requireContext()
        )
        recViewPhotos.adapter = recViewPhotosAdapter
        val photoItemBottomMargin =
            resources.getDimensionPixelOffset(R.dimen.photo_item_bottom_margin)
        val photoItemHorizontalMargin =
            resources.getDimensionPixelOffset(R.dimen.photo_item_horizontal_margin)
        recViewPhotos.addItemDecoration(
            PhotoItemDecoration(
                photoItemBottomMargin, photoItemHorizontalMargin
            )
        )

        recViewPhotosAdapter.isAllItemsVisibleLive.observe(this) { isAllItemsVisible ->
            if (isAllItemsVisible) {
                progressBar.visibility = GONE
            } else {
                progressBar.visibility = VISIBLE
            }
        }

        vm.photosLive.observe(this) { photos ->
            recViewPhotosAdapter.setPhotos(photos)
        }
        vm.loadCuratedPhotos()

        // SEARCH
        val searchView = view.findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (this@HomeFragment::textChangedJob.isInitialized) {
                    textChangedJob.cancel()
                }

                textChangedJob = lifecycleScope.launch {
                    delay(1500L)
                    if (newText != null) {
                        vm.findPhotos(newText)
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    vm.findPhotos(query)
                }
                searchView.clearFocus()
                return true
            }
        })

        return view
    }
}