package com.example.bob_friend_android.ui.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.ui.adapter.BoardAdapter
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentSetListBinding
import com.example.bob_friend_android.ui.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class SetListFragment : BaseFragment<FragmentSetListBinding>(
    R.layout.fragment_set_list
) {
    private val viewModel by activityViewModels<ListViewModel>()

    private lateinit var boardAdapter: BoardAdapter
    private lateinit var boardArrayList : ArrayList<Board>
    private var listPage by Delegates.notNull<Int>()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSetListBinding {
        return FragmentSetListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        requireDataBinding().recyclerview.layoutManager = LinearLayoutManager(requireActivity())
        boardAdapter = BoardAdapter()
        requireDataBinding().recyclerview.adapter = boardAdapter

        listPage = 0
        boardArrayList = ArrayList()
        viewModel.setAppointmentList(listPage)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(requireDataBinding().tbMain)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        requireDataBinding().recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                // 스크롤이 끝에 도달했는지 확인
//                if (!requireDataBinding().recyclerview.canScrollVertically(1)) {
//                    listPage++
//                    viewModel.setAppointmentList(listPage)
//                }
//            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                    Log.d("map_scroll", "EventOccurs")
                    listPage++
                    viewModel.setAppointmentList(listPage)
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

        requireDataBinding().tbMain.setOnClickListener {
            activity?.let {
                goToNext(R.id.action_setListFragment_to_searchBoardFragment)
            }

        }

        val swipe = requireDataBinding().layoutSwipe
        swipe.setOnRefreshListener {
            listPage = 0
            boardArrayList.clear()
            viewModel.setAppointmentList(listPage)
            swipe.isRefreshing = false
        }

        observeData()
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@SetListFragment, object : Observer<String> {
                override fun onChanged(t: String?) {
                    if (t == "Access Denied") {
//                        viewModel.refreshToken(App.prefs.getString("token", "")!!, App.prefs.getString("refresh", "")!!)
                    }
                    if (t != null) {
                        showToast(t)
                    }
                }
            })

            appointmentList.observe(viewLifecycleOwner) {
                for(document in it.boardList) {
                    boardArrayList.add(document)
                }
                boardAdapter.setItems(boardArrayList)
            }

            val dialog = SetLoadingDialog(requireContext())
            isLoading.observe(viewLifecycleOwner) {
                if (isLoading.value!!) {
                    dialog.show()
                }
                else if (!isLoading.value!!) {
                    dialog.dismiss()
                }
            }
        }
    }
}