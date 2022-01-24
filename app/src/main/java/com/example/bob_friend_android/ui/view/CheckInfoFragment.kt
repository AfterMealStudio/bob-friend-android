package com.example.bob_friend_android.ui.view

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentCheckInfoBinding
import com.example.bob_friend_android.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckInfoFragment : BaseFragment<FragmentCheckInfoBinding>(
    R.layout.fragment_check_info
) {
    private val viewModel by activityViewModels<LoginViewModel>()

    override fun init() {
        observeData()

        val gender = App.prefs.getString("sex","")
        var userGender = "NONE"
        when (gender) {
            "MALE" -> {
                userGender = "남성"
            }
            "FEMALE" -> {
                userGender = "여성"
            }
            "NONE" -> {
                userGender = "비공개"
            }
        }

        binding.tvEmail.text = App.prefs.getString("email", "")
        binding.tvUsername.text = App.prefs.getString("nickname","")
        binding.tvBirth.text = App.prefs.getString("age","")
        binding.tvGender.text = userGender
        binding.ratingBar.rating = App.prefs.getFloat("rating",0.0F)

        binding.btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("로그아웃")
            builder.setMessage("로그아웃을 하시겠습니까?")

            builder.setPositiveButton("예") { dialog, which ->
                val editor = App.prefs.edit()
                editor.clear()
                editor.apply()
//                goToNext(R.id.action_checkInfoFragment_to_loginFragment)
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()
        }

        binding.btnDeleteUser.setOnClickListener {
            goToNext(R.id.action_checkInfoFragment_to_deleteUserFragment)
        }

        binding.btnMyList.setOnClickListener {
            goToNext(R.id.action_checkInfoFragment_to_myBoardFragment, "owned")
        }

        binding.btnMyAppointmentList.setOnClickListener {
            goToNext(R.id.action_checkInfoFragment_to_myBoardFragment, "joined")
        }

        binding.btnIntroduceTeam.setOnClickListener {
            goToNext(R.id.action_checkInfoFragment_to_introduceTeamFragment)
        }

        binding.btnUpdateUserInfo.setOnClickListener {
            goToNext(R.id.action_checkInfoFragment_to_updateUserInfoFragment)
        }
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }
        }
    }
}