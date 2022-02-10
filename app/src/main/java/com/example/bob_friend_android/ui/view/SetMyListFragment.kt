package com.example.bob_friend_android.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.ui.adapter.BoardAdapter
import com.example.bob_friend_android.databinding.FragmentSetMyListBinding
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.ui.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class SetMyListFragment : BaseFragment<FragmentSetMyListBinding>(
    R.layout.fragment_set_my_list
) {
    private val viewModel by activityViewModels<ListViewModel>()

    private var listPage = 0 // 현재 페이지
    private var type : String = ""

    private lateinit var boardAdapter: BoardAdapter
    private var boardArrayList : ArrayList<Board> = ArrayList()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSetMyListBinding {
        return FragmentSetMyListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(requireDataBinding().toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

//        binding.myboardToolbar.navigationIcon?.apply {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_IN)
//            }else{
//                setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
//            }
//        }

        requireDataBinding().rvBoard.layoutManager = LinearLayoutManager(requireContext())
        boardAdapter = BoardAdapter()
        requireDataBinding().rvBoard.adapter = boardAdapter

//        if(intent.hasExtra("type")){
//            type = intent.getStringExtra("type").toString()
//
//            viewModel.setList(type = type, listPage = listPage)
//
//            binding.rvBoard.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    // 스크롤이 끝에 도달했는지 확인
//                    if (!binding.rvBoard.canScrollVertically(1)) {
//                        listPage++
//                        viewModel.setList(type = type, listPage = listPage)
//                    }
//                }
//            })
//        }

        boardAdapter.setOnItemClickListener(object : BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, pos: Int) {
                val action =
                    SetMyListFragmentDirections.actionSetMyListFragmentToSetBoardFragment(data.id.toString())
                findNavController().navigate(action)
            }
        })

        observeData()
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }

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