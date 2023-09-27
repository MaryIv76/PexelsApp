package com.ivanova.pexelsapp.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ivanova.pexelsapp.R
import com.ivanova.pexelsapp.View.RecyclerViews.BookmarksPhotosRecyclerViewAdapter
import com.ivanova.pexelsapp.ViewModel.MainViewModel


class BookmarksFragment : Fragment() {

    private lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false)

        val recViewPhotos: RecyclerView = view.findViewById(R.id.recView_bookmarksPhotos)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarBookmarks)
        var btnExplore: TextView = view.findViewById(R.id.tv_exploreBookmarks)


        // PHOTOS
        val photosLayoutManager: StaggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recViewPhotos.layoutManager = photosLayoutManager
        val recViewPhotosAdapter = BookmarksPhotosRecyclerViewAdapter(requireContext())
        recViewPhotos.adapter = recViewPhotosAdapter

        recViewPhotosAdapter.onItemClick = { photoId ->
            findNavController().navigate(
                BookmarksFragmentDirections.actionBookmarksFragmentToDetailsFragment(
                    photoId, "Bookmarks"
                )
            )
        }

        recViewPhotosAdapter.isAllItemsVisibleLive.observe(this) { isAllItemsVisible ->
            if (isAllItemsVisible) {
                progressBar.visibility = View.GONE
            } else {
                progressBar.visibility = View.VISIBLE
            }
        }

        vm.allPhotosFromBookmarksLive.observe(this) { photos ->
            val stubLayout: RelativeLayout = view.findViewById(R.id.relLayout_stubBookmarks)
            recViewPhotosAdapter.setPhotos(photos)
            if (photos.size == 0) {
                stubLayout.visibility = View.VISIBLE
            } else {
                stubLayout.visibility = View.GONE
            }
        }

        vm.loadAllPhotosFromBookmarks()


        btnExplore.setOnClickListener {
            findNavController().navigate(BookmarksFragmentDirections.actionBookmarksFragmentToHomeFragment())
        }


        return view
    }

}