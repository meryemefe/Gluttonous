package com.meryemefe.gluttonous.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.meryemefe.gluttonous.R

class FragmentHome : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Set the slideshow by starting AnimationDrawable
        val imageView = view.findViewById<ImageView>(R.id.imageViewSlide)
        val animationDrawable = imageView.drawable as AnimationDrawable
        animationDrawable.start()

        return view
    }

}