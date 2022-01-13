package com.example.bob_friend_android.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentDeleteUserBinding
import com.example.bob_friend_android.viewmodel.UserViewModel

class DeleteUserFragment : Fragment() {

    private lateinit var binding: FragmentDeleteUserBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var token: String
    var toast:Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delete_user, container, false)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.title = "회원탈퇴"

        observeData()

        token = App.prefs.getString("token", "").toString()

        binding.deleteUserBtn.setOnClickListener {
            viewModel.deleteUser(token, binding.editTextPassword.text.toString())
            val editor = App.prefs.edit()
            editor.clear()
            editor.apply()
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
        }

        binding.deleteLayout.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextPassword.windowToken, 0)
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

//    @SuppressLint("ShowToast")
//    private fun showToast(msg: String) {
//        if (toast == null) {
//            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
//        } else toast?.setText(msg)
//        toast?.show()
//    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
//                showToast(it)
            }
        }
    }
}