package com.example.bob_friend_android.ui.view

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.App
import com.example.bob_friend_android.ui.adapter.BoardAdapter
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentSetListBinding
import com.example.bob_friend_android.data.entity.Token
import com.example.bob_friend_android.ui.viewmodel.ListViewModel
import java.util.*

class SetListFragment : BaseFragment<FragmentSetListBinding>(
    R.layout.fragment_set_list
) {
    private val viewModel by activityViewModels<ListViewModel>()

    private lateinit var boardAdapter: BoardAdapter
    private var boardArrayList : ArrayList<Board> = ArrayList()
    private var listPage = 0 // 현재 페이지

    override fun init() {
        val swipe = binding.layoutSwipe
        swipe.setOnRefreshListener {
            listPage = 0
            boardArrayList.clear()
            viewModel.setList(listPage)
            swipe.isRefreshing = false
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
        boardAdapter = BoardAdapter()
        binding.recyclerview.adapter = boardAdapter

        viewModel.setList(listPage)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.tbMain)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.recyclerview.canScrollVertically(1)) {
                    listPage++
                    viewModel.setList(listPage)
                }
            }
        })

        boardAdapter.setOnItemClickListener(object : BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, pos: Int) {
                activity?.let {
//                    goToNext(R.id.action_setListFragment_to_setBoardFragment, boardId = data.id)
                    val action =
                        SetListFragmentDirections.actionSetListFragmentToSetBoardFragment(data.id.toString())
                    findNavController().navigate(action)
                }
            }
        })

        binding.tbMain.setOnClickListener {
            activity?.let {
                goToNext(R.id.action_setListFragment_to_searchBoardFragment)
            }

        }

        observeData()
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@SetListFragment, object : Observer<String> {
                override fun onChanged(t: String?) {
                    if (t == "Access Denied") {
                        viewModel.refreshToken(App.prefs.getString("token", "")!!, App.prefs.getString("refresh", "")!!)
                    }
                    if (t != null) {
                        showToast(t)
                    }
                }
            })

            boardList.observe(viewLifecycleOwner) {
                for(document in it.boardList) {
                    boardArrayList.add(document)
                }
                boardAdapter.addItems(boardArrayList)
            }

            refreshToken.observe(this@SetListFragment, object : Observer<Token> {
                override fun onChanged(t: Token) {
                    val editor = App.prefs.edit()
                    editor.putString("token", t.accessToken)
                    editor.putString("refresh", t.refreshToken)
                    editor.putBoolean("checked", true)
                    editor.apply()
                }
            })

            val dialog = SetLoadingDialog(requireContext())
            progressVisible.observe(viewLifecycleOwner) {
                if (progressVisible.value!!) {
                    dialog.show()
                }
                else if (!progressVisible.value!!) {
                    dialog.dismiss()
                }
            }
        }
    }
}