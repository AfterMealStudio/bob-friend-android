package com.example.bob_friend_android.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentUpdatePasswordBinding
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordFragment : BaseFragment<FragmentUpdatePasswordBinding>(
    R.layout.fragment_update_password
) {
    private val viewModel by activityViewModels<UserViewModel>()

    private var password: String? = null
    private var passwordCheck: String? = null

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdatePasswordBinding {
        return FragmentUpdatePasswordBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(requireDataBinding().toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        requireDataBinding().toolbar.title = "회원정보수정"

        observeData()

        requireDataBinding().btnUpdateUserInfo.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("회원정보수정")
            builder.setMessage("이렇게 정보 변경을 진행할까요?")

            if (requireDataBinding().etvPassword.text!!.isNotBlank()){
                password = requireDataBinding().etvPassword.text.toString().trim()
            }
            if (requireDataBinding().etvPasswordCheck.text!!.isNotBlank()){
                passwordCheck = requireDataBinding().etvPasswordCheck.text.toString().trim()
            }

            builder.setPositiveButton("예") { dialog, which ->
                if(viewModel.validationUpdatePassword(
                        password = password,
                        passwordCheck = passwordCheck)){
                    viewModel.updateUser(agree = null, email = null, nickname = null,
                        password = password, sex = null, birth = null
                    )
                }

                findNavController().popBackStack()
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()
        }

        requireDataBinding().layoutUpdate.setOnClickListener {
            hideKeyboard()
        }
    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireDataBinding().etvPassword.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().etvPasswordCheck.windowToken, 0)


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

            userInfo.observe(viewLifecycleOwner, { user ->
                val editor = App.prefs.edit()
                editor.putInt("id", user.id)
                editor.putString("email", user.email)
                editor.putString("nickname", user.nickname)
                editor.putString("age", user.age)
                editor.putString("sex", user.sex)
                editor.putBoolean("agree", user.agree)
                editor.putFloat("rating", user.rating)
                editor.apply()
            })
        }
    }
}