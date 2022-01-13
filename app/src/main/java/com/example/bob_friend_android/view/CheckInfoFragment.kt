package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentCheckInfoBinding
import com.example.bob_friend_android.view.*
import com.example.bob_friend_android.viewmodel.LoginViewModel

class CheckInfoFragment : Fragment() {
    private lateinit var binding: FragmentCheckInfoBinding
    private lateinit var viewModel: LoginViewModel
    var toast: Toast? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_info, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.logout = viewModel

        observeData()

        val gender = App.prefs.getString("sex","")
        var userGender = "NONE"
        if (gender=="MALE") {
            userGender = "남성"
        }
        else if (gender=="FEMALE") {
            userGender = "여성"
        }
        else if (gender=="NONE") {
            userGender = "비공개"
        }
            binding.headerEmail.text = App.prefs.getString("email", "")
            binding.headerUsername.text = App.prefs.getString("nickname","")
            binding.headerBirth.text = App.prefs.getString("age","")
            binding.headerGender.text = userGender
            binding.ratingBar.rating = App.prefs.getFloat("rating",0.0F)

            binding.logoutBtn.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("로그아웃")
                builder.setMessage("로그아웃을 하시겠습니까?")

                builder.setPositiveButton("예") { dialog, which ->
                    val editor = App.prefs.edit()
                    editor.clear()
                    editor.apply()
//                    val intent = Intent(requireContext(), LoginActivity::class.java)
//                    startActivity(intent)
                }
                builder.setNegativeButton("아니오") { dialog, which ->
                    return@setNegativeButton
                }
                builder.show()
            }

            binding.deleteUserBtn.setOnClickListener {
//                val intent = Intent(requireContext(), DeleteUserActivity::class.java)
//                startActivity(intent)
            }

            binding.myBoardList.setOnClickListener {
//                val intent = Intent(requireContext(), MyBoardActivity::class.java)
//                intent.putExtra("type", "owned")
//                startActivity(intent)
            }

            binding.myAppointmentList.setOnClickListener {
//                val intent = Intent(requireContext(), MyBoardActivity::class.java)
//                intent.putExtra("type", "joined")
//                startActivity(intent)
            }

            binding.settingAbout.setOnClickListener {
//                val intent = Intent(requireContext(), AboutActivity::class.java)
//                startActivity(intent)
            }

            binding.updateUserInfo.setOnClickListener {
//                val intent = Intent(requireContext(), UpdateUserInfoActivity::class.java)
//                startActivity(intent)
            }

            return binding.root
        }

    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }
        }
    }
}