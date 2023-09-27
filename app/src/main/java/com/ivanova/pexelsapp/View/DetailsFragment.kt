package com.ivanova.pexelsapp.View

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ivanova.pexelsapp.Model.Photo
import com.ivanova.pexelsapp.R
import com.ivanova.pexelsapp.ViewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL


class DetailsFragment : Fragment() {

    val args: DetailsFragmentArgs by navArgs()

    private lateinit var vm: MainViewModel
    private lateinit var photo: Photo

    private lateinit var progressBar: ProgressBar


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
        val tvPhotographer: TextView = view.findViewById(R.id.tv_photographer)
        val imViewPhoto: ImageView = view.findViewById(R.id.imView_photo)
        val btnDownload: ImageButton = view.findViewById(R.id.btn_download)
        progressBar = view.findViewById(R.id.progressBarDetails)


        // PROGRESS BAR
        progressBar.visibility = View.VISIBLE


        // Button back
        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        // Load photo and photographer
        vm.photoDetailsLive.observe(this) { photo ->
            this.photo = photo
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


        // Button download
        btnDownload.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                )
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    if (photo != null) {
                        requireActivity().runOnUiThread {
                            progressBar.visibility = VISIBLE
                        }

                        val url = URL(photo.src.original)
                        val imageData = url.readBytes()
                        val bitmap: Bitmap =
                            BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                        saveImageToDownloadFolder(photo.photographer + ".jpeg", bitmap)
                    }
                }
            }
        }

        return view
    }

    private fun saveImageToDownloadFolder(imageFile: String, bitmap: Bitmap) {
        try {
            val filePath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                imageFile
            )
            val outputStream: OutputStream = FileOutputStream(filePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "$imageFile was successfully saved in Download Folder",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            requireActivity().runOnUiThread {
                progressBar.visibility = GONE
            }
        }
    }
}
