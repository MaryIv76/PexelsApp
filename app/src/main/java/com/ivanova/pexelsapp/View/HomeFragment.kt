package com.ivanova.pexelsapp.View

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ivanova.pexelsapp.R
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
        val recViewTitles: RecyclerView = view.findViewById(R.id.recView_titles)
        val recViewPhotos: RecyclerView = view.findViewById(R.id.recView_photos)
        val searchView: SearchView = view.findViewById(R.id.search_view)
        val tvExplore: TextView = view.findViewById(R.id.tv_explore)


        // PROGRESS BAR
        progressBar.visibility = GONE


        // TITLES
        recViewTitles.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        val recViewTitlesAdapter = TitlesRecyclerViewAdapter()
        recViewTitles.adapter = recViewTitlesAdapter
        val titleItemMargin = resources.getDimensionPixelOffset(R.dimen.title_item_margin)
        recViewTitles.addItemDecoration(TitleItemDecoration(titleItemMargin))

        recViewTitlesAdapter.onItemClick = { title ->
            vm.findPhotos(title)
            searchView.setQuery(title, false)
        }

        vm.titlesLive.observe(this) { titles ->
            recViewTitlesAdapter.setTitles(titles)

            if (titles.size == 0) {
                recViewTitles.visibility = GONE

                val params = recViewTitles.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 0, 0, 0)
                recViewTitles.layoutParams = params
            } else {
                recViewTitles.visibility = VISIBLE

                val params = recViewTitles.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, resources.getDimension(R.dimen.titles_top_margin).toInt(), 0, 0)
                recViewTitles.layoutParams = params
            }
        }
        vm.loadTitles()

        vm.currentSearchRequestLive.observe(this) { request ->
            recViewTitlesAdapter.setCurrentRequest(request)
        }


        // PHOTOS
        val photosLayoutManager: StaggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recViewPhotos.layoutManager = photosLayoutManager
        val recViewPhotosAdapter = PhotosRecyclerViewAdapter(
            requireContext()
        )
        recViewPhotos.adapter = recViewPhotosAdapter

        recViewPhotosAdapter.isAllItemsVisibleLive.observe(this) { isAllItemsVisible ->
            if (isAllItemsVisible) {
                progressBar.visibility = GONE
            } else {
                progressBar.visibility = VISIBLE
            }
        }

        vm.photosLive.observe(this) { photos ->
            val stubLayout: RelativeLayout = view.findViewById(R.id.relLayout_Stub)
            recViewPhotosAdapter.setPhotos(photos)
            if (photos.size == 0) {
                stubLayout.visibility = VISIBLE
            } else {
                stubLayout.visibility = GONE
            }
        }
        vm.loadCuratedPhotos()


        // SEARCH
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


        // STUB
        tvExplore.setOnClickListener {
            vm.loadCuratedPhotos()
            searchView.setQuery("", false)
        }

        return view
    }

}