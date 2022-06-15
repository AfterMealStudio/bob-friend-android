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
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentUpdateUserInfoBinding
import com.example.bob_friend_android.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateUserInfoFragment : BaseFragment<FragmentUpdateUserInfoBinding>(
    R.layout.fragment_update_user_info
) {
    private val viewModel by activityViewModels<UserViewModel>()

    private var nickname: String? = null
    private var dateBirth: String? = null
    private var gender : String? = null

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdateUserInfoBinding {
        return FragmentUpdateUserInfoBinding.inflate(inflater, container, false).apply {
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

        requireDataBinding().rgGender.setOnCheckedChangeListener { group, checkedId ->
            gender = when(checkedId) {
                R.id.male -> "MALE"
                R.id.female -> "FEMALE"
                R.id.third_gender -> "NONE"
                else -> ""
            }
        }

        var nicknameCheck = false
        requireDataBinding().btnNicknameCheck.setOnClickListener {
            nickname = requireDataBinding().etvNickname.text.toString().trim()
            viewModel.checkUserNickname(nickname!!)
            nicknameCheck = true
        }

        requireDataBinding().btnUpdateUserInfo.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("회원정보수정")
            builder.setMessage("이렇게 정보 변경을 진행할까요?")

            if(requireDataBinding().etvNickname.text!!.isNotBlank()){
                nickname = requireDataBinding().etvNickname.text.toString().trim()
            }
            if (requireDataBinding().etvDateBirth.text!!.isNotBlank()){
                dateBirth = requireDataBinding().etvDateBirth.text.toString().trim()
            }

            builder.setPositiveButton("예") { dialog, which ->
                if(viewModel.validationUpdate(
                        nicknameCheck = nicknameCheck, nickname = nickname,
                        gender = gender,
                        dateBirth = dateBirth)){
                    viewModel.updateUser(agree = null, email = null, nickname = nickname,
                        password = null, sex = gender, birth = dateBirth
                    )
                }

//                findNavController().popBackStack()
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
        imm.hideSoftInputFromWindow(requireDataBinding().etvNickname.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().etvDateBirth.windowToken, 0)
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
            errorMsg.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    showToast(it)
                }
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