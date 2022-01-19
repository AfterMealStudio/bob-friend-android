package com.example.bob_friend_android.ui.view

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentDeleteUserBinding
import com.example.bob_friend_android.ui.viewmodel.UserViewModel

class DeleteUserFragment : BaseFragment<FragmentDeleteUserBinding>(
    R.layout.fragment_delete_user
) {
    private val viewModel by activityViewModels<UserViewModel>()
    private lateinit var token: String

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.title = "회원탈퇴"

        observeData()

        token = App.prefs.getString("token", "").toString()

        binding.btnDeleteUser.setOnClickListener {
            viewModel.deleteUser(token, binding.etvPassword.text.toString())
            val editor = App.prefs.edit()
            editor.clear()
            editor.apply()
            goToNext(R.id.action_deleteUserFragment_to_loginFragment)
        }

        binding.layoutDelete.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etvPassword.windowToken, 0)
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