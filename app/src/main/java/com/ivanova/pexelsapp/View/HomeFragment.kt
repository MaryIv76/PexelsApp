package com.ivanova.pexelsapp.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast.LENGTH_LONG
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ivanova.pexelsapp.Model.RetrofitInstance
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

        RetrofitInstance.Companion.setContext(requireContext())

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
        val networkStubLayout: RelativeLayout = view.findViewById(R.id.relLayout_networkStub)
        val tvTryAgain: TextView = view.findViewById(R.id.tv_tryAgain)


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
            searchView.setQuery(title, true)
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
                params.setMargins(
                    0,
                    resources.getDimension(R.dimen.titles_top_margin).toInt(),
                    0,
                    0
                )
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

        recViewPhotosAdapter.onItemClick = { photoId ->
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment(photoId))
        }

        recViewPhotosAdapter.isAllItemsVisibleLive.observe(this) { isAllItemsVisible ->
            if (isAllItemsVisible) {
                progressBar.visibility = GONE
            } else {
                progressBar.visibility = VISIBLE
            }
        }

        vm.photosLive.observe(this) { photos ->
            val stubLayout: RelativeLayout = view.findViewById(R.id.relLayout_stub)
            recViewPhotosAdapter.setPhotos(photos)
            if (photos.size == 0) {
                stubLayout.visibility = VISIBLE
            } else {
                stubLayout.visibility = GONE
            }
        }

        if (vm.currentSearchRequestLive.value == null || vm.currentSearchRequestLive.value.toString() == "") {
            vm.loadCuratedPhotos()
        }


        // SEARCH
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (this@HomeFragment::textChangedJob.isInitialized) {
                    textChangedJob.cancel()
                }

                textChangedJob = lifecycleScope.launch {
                    delay(1500L)
                    if (newText != null && newText != "") {
                        vm.findPhotos(newText)
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (this@HomeFragment::textChangedJob.isInitialized) {
                    textChangedJob.cancel()
                }

                if (query != null && query != "") {
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


        // NETWORK STUB
        vm.noInternetConnectionLive.observe(this) { noNetwork ->
            if (noNetwork) {
                networkStubLayout.visibility = VISIBLE
                progressBar.visibility = GONE
                recViewPhotos.visibility = GONE
            } else {
                networkStubLayout.visibility = GONE
                progressBar.visibility = VISIBLE
                recViewPhotos.visibility = VISIBLE

                if (recViewTitles.isGone) {
                    vm.loadTitles()
                }
            }
        }

        tvTryAgain.setOnClickListener {
            progressBar.visibility = VISIBLE

            val request: String = searchView.query.toString().trim()
            if (request == "") {
                vm.loadCuratedPhotos()
            } else {
                vm.findPhotos(request)
            }
        }


        // NETWORK REQUEST ERROR
        vm.errorRequestLive.observe(this) { isRequestError ->
            if (isRequestError) {
                val toast = Toast.makeText(requireContext(), R.string.request_error, LENGTH_LONG)
                toast.show()
            }
        }

        vm.someNetworkErrorLive.observe(this) { isSomeNetworkError ->
            if (isSomeNetworkError) {
                val toast =
                    Toast.makeText(requireContext(), R.string.network_request_error, LENGTH_LONG)
                toast.show()
            }
        }

        return view
    }

}