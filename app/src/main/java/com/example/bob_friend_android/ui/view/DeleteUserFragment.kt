package com.example.bob_friend_android.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentBoardBinding
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentDeleteUserBinding
import com.example.bob_friend_android.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteUserFragment : BaseFragment<FragmentDeleteUserBinding>(
    R.layout.fragment_delete_user
) {
    private val viewModel by activityViewModels<UserViewModel>()
    private lateinit var token: String

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeleteUserBinding {
        return FragmentDeleteUserBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(requireDataBinding().toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        requireDataBinding().toolbar.title = "회원탈퇴"

        observeData()

        token = App.prefs.getString("token", "").toString()

        requireDataBinding().btnDeleteUser.setOnClickListener {
            viewModel.deleteUser(token, requireDataBinding().etvPassword.text.toString())
            val editor = App.prefs.edit()
            editor.clear()
            editor.apply()
            goToNext(R.id.action_deleteUserFragment_to_loginFragment)
        }

        requireDataBinding().layoutDelete.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireDataBinding().etvPassword.windowToken, 0)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }
        }
    }
}