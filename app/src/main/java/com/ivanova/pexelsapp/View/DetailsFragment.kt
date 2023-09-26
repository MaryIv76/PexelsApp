package com.ivanova.pexelsapp.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.ivanova.pexelsapp.R

class DetailsFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)


        val backBtn: ImageButton = view.findViewById(R.id.btn_back)


        backBtn.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }


        return view
    }
}