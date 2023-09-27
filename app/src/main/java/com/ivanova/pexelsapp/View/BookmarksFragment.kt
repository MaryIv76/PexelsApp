package com.ivanova.pexelsapp.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.room.Room
import com.ivanova.pexelsapp.Model.Database.AppDatabase
import com.ivanova.pexelsapp.Model.Database.PhotoEntity
import com.ivanova.pexelsapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarksFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false)

        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "PexelsDB"
            ).build()

            val photoDao = db.photoDao()


            val photos: List<PhotoEntity> = photoDao.getAllPhotos()
            photos.size

        }

        return view
    }

}