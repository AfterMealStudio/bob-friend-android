package com.example.bob_friend_android.view

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.ui.adapter.BoardAdapter
import com.example.bob_friend_android.databinding.FragmentSetMyListBinding
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.viewmodel.ListViewModel
import java.util.ArrayList

class SetMyListFragment(override val viewModel: ListViewModel) : BaseFragment<FragmentSetMyListBinding, ListViewModel>(
    R.layout.fragment_set_my_list
) {
    private var listPage = 0 // 현재 페이지
    private var type : String = ""

    private lateinit var boardAdapter: BoardAdapter
    private var boardArrayList : ArrayList<Board> = ArrayList()

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
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

        binding.rvBoard.layoutManager = LinearLayoutManager(requireContext())
        boardAdapter = BoardAdapter()
        binding.rvBoard.adapter = boardAdapter

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
                goToNext(R.id.action_setMyListFragment_to_setBoardFragment, boardId = data.id)
            }
        })

        observeData()
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }

            boardList.observe(viewLifecycleOwner) {
                for(document in it.boardList) {
                    boardArrayList.add(document)
                }
                boardAdapter.addItems(boardArrayList)
            }

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