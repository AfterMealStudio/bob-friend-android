package com.example.bob_friend_android.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentBoardBinding
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentFindUserAccountBinding
import com.example.bob_friend_android.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindUserAccountFragment : BaseFragment<FragmentFindUserAccountBinding>(
    R.layout.fragment_find_user_account
) {
    private val viewModel by activityViewModels<UserViewModel>()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFindUserAccountBinding {
        return FragmentFindUserAccountBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        requireDataBinding().btnEmailCheck.setOnClickListener {
            viewModel.checkUserEmail(requireDataBinding().etvEmail.text.toString().trim())
        }

        requireDataBinding().btnChangePassword.setOnClickListener {
            val email = requireDataBinding().etvEmail2.text.toString().trim()
            val birth = requireDataBinding().etvDateBirth.text.toString().trim()
            viewModel.updateUserPassword(email, birth)
        }

        requireDataBinding().layoutFind.setOnClickListener {
            hideKeyboard()
        }

        observeData()
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }
        }
    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireDataBinding().etvEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().etvEmail2.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().etvDateBirth.windowToken, 0)
    }
}