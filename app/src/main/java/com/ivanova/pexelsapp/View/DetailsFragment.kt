package com.ivanova.pexelsapp.View

import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ivanova.pexelsapp.R
import com.ivanova.pexelsapp.ViewModel.MainViewModel
import java.util.zip.GZIPOutputStream

class DetailsFragment : Fragment() {

    val args: DetailsFragmentArgs by navArgs()

    private lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)


        val backBtn: ImageButton = view.findViewById(R.id.btn_back)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarDetails)
        val tvPhotographer: TextView = view.findViewById(R.id.tv_photographer)
        val imViewPhoto: ImageView = view.findViewById(R.id.imView_photo)


        // PROGRESS BAR
        progressBar.visibility = View.VISIBLE


        // Button back
        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        // Load photo and photographer
        vm.photoDetailsLive.observe(this) { photo ->
            tvPhotographer.text = photo.photographer
            Glide.with(this)
                .load(photo.src.original)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(ContentValues.TAG, "onLoadFailed")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = GONE
                        return false
                    }
                })
                .into(imViewPhoto)
        }


        // Get Photo ID
        val photoId: Int = args.photoId
        vm.loadPhotoById(photoId)

        return view
    }
}