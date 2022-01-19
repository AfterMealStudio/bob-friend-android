     package com.example.bob_friend_android.ui.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bob_friend_android.data.entity.SearchLocation

     abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : Fragment() {
    protected lateinit var binding: B

//    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        init()
//        navController = Navigation.findNavController(view)
    }

    abstract fun init()

    protected fun showToast(msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    protected fun goToNext(id: Int, type: String? = null, location: SearchLocation? = null) {
        when {
            type != null -> {
                val bundle = bundleOf("type" to type)
                findNavController().navigate(id, bundle)
            }
            location != null -> {
                val bundle = bundleOf("location" to location.address, "name" to location.name, "longitude" to location.x, "latitude" to location.y)
                findNavController().navigate(id, bundle)
            }
            else -> {
                findNavController().navigate(id)
            }
        }
    }
}