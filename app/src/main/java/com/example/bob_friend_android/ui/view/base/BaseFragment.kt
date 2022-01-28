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
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.bob_friend_android.data.entity.SearchLocation

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : Fragment() {

    private var _binding : B? = null
    protected val binding : B?
        get() = _binding

    abstract fun onCreateBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun init()

    private var toast : Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return onCreateBinding(inflater, container)
            .also { _binding = it}
            .root
            .also { ViewTreeLifecycleOwner.set(it, viewLifecycleOwner)}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
//        navController = Navigation.findNavController(view)
    }

    protected fun requireDataBinding(): B {
        if (_binding == null) {
            throw IllegalStateException(
                "BaseFragment $this did not return a Binding from onCreateview"
            )
        }
        return _binding!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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


    protected fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }
}