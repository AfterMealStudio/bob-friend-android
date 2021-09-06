package com.example.bob_friend_android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import com.example.bob_friend_android.R
import com.example.bob_friend_android.onBackPressedListener

class AboutFragment : Fragment(){
    lateinit var myView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.fragment_about, container, false)

        return myView
    }
}