package com.example.bob_friend_android.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.ui.adapter.CommentAdapter
import com.example.bob_friend_android.ui.adapter.UserAdapter
import com.example.bob_friend_android.databinding.FragmentSetBoardBinding
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.data.entity.Comment
import com.example.bob_friend_android.data.entity.UserItem
import com.example.bob_friend_android.ui.viewmodel.BoardViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import java.util.*
import kotlin.collections.ArrayList


class SetBoardFragment : BaseFragment<FragmentSetBoardBinding>(
    R.layout.fragment_set_board
), OnMapReadyCallback {
    private val viewModel by activityViewModels<BoardViewModel>()

    val args : SetBoardFragmentArgs by navArgs()
    private var detailBoardId : Int = 0
    private var detailCommentId : Int = 0

    private val commentList : ArrayList<Comment> = ArrayList()
    private val userList : ArrayList<UserItem> = ArrayList()
    private var commentAdapter = CommentAdapter(commentList, detailBoardId)
    private val userAdapter = UserAdapter(userList)

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    private var participate = "참가하기"

    var appointmentTime = ""

    var inputMethodManager: InputMethodManager? = null

    var selectedId: Int? = null
    var flag: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_board, container, false)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return binding.root
    }


    override fun init() {
        inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.tbBoard)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        commentAdapter.commentClick = object : CommentAdapter.CommentClick {
            override fun onCommentClick(view: View, position: Int, comment: Comment) {
                withCommentItems(comment.id, comment.author!!.id, true, null, position)
            }
        }

        commentAdapter.reCommentClick = object : CommentAdapter.ReCommentClick {
            override fun onReCommentClick(
                view: View,
                position: Int,
                commentId: Int,
                reComment: Comment
            ) {
                withCommentItems(commentId, reComment.author!!.id, false, reComment.id, null)
            }
        }

        observeData()

        if(args.boardId != null) {
            detailBoardId = args.boardId!!.toInt()

            val swipe = binding.layoutSwipe
            swipe.setOnRefreshListener {
                viewModel.readBoard(detailBoardId)
                swipe.isRefreshing = false
            }

            binding.btnParticipate.setOnClickListener {
                if(binding.btnParticipate.text=="마감하기"){
                    viewModel.closeBoard(detailBoardId)
                }
                else {
                    viewModel.participateBoard(detailBoardId)
                }
            }

            viewModel.readBoard(detailBoardId)
        }

//        binding.backBtn.setOnClickListener {
//            onBackPressed()
//        }

        binding.etvComment.setOnEditorActionListener{ textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // 키보드 내리기
                inputMethodManager!!.hideSoftInputFromWindow(binding.etvComment.windowToken, 0)
                handled = true
            }

            handled
        }
    }

    private fun makeBuilder(title: String, content:String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(content)

        if (title == "약속 마감" || title == "접근할 수 없는 약속" || title == "마감하기") {
            builder.setPositiveButton("확인") { dialog, which ->
                val intent = Intent().apply {
                    putExtra("CallType", "close")
                }
//                setResult(RESULT_OK, intent)
//                if(!isFinishing) finish()
            }
        }
        else {
            builder.setPositiveButton("확인") { dialog, which ->
                return@setPositiveButton
            }
        }
        builder.show()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.option_menu, menu)
//        if (userId == App.prefs.getInt("id", 0)){
//            menu?.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "삭제하기")
//        }
//        else {
//            Log.d(TAG, "userId: $userId")
//            menu?.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, "신고하기")
//        }
//        return true
//    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            Menu.FIRST + 1 -> {
                viewModel.deleteBoard(detailBoardId)
//                val intent = Intent().apply {
//                    putExtra("CallType", "delete")
//                }
//                setResult(RESULT_OK, intent)
//                if(!isFinishing) finish()
            }
            Menu.FIRST + 2 -> {
                viewModel.reportBoard(detailBoardId)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun withCommentItems(
        commentId: Int,
        commentWriterId: Int,
        Comment: Boolean,
        recommentId: Int?,
        position: Int?
    ) {
        var writer = false

        if (commentWriterId == App.prefs.getInt("id", -1)){
            writer = true
        }

        val dialog = SetOptionDialog(writer, Comment)
        // 버튼 클릭 이벤트 설정
        dialog.setButtonClickListener(object : SetOptionDialog.OnButtonClickListener {
            override fun onAddReCommentClicked() {
                if (position != null) {
                    if (selectedId!=null) {
                        binding.rvComment[selectedId!!].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    selectedId = position
                    binding.rvComment[position].setBackgroundColor(Color.parseColor("#DADAEC"))
//                    binding.commentRecyclerview.setBackgroundColor(Color.WHITE)
//                    showSoftInput()
                    detailCommentId = commentId
                    flag = true
                }
            }

            override fun onReportCommentClicked() {
                if (Comment) {
                    viewModel.reportComment(detailBoardId, commentId)
                } else {
                    if (recommentId != null) {
                        viewModel.reportReComment(detailBoardId, commentId, recommentId)
                    }
                }
            }

            override fun onDeleteCommentClicked() {
                if (Comment) {
                    viewModel.deleteComment(detailBoardId, commentId)
                } else {
                    if (recommentId != null) {
                        viewModel.deleteReComment(detailBoardId, commentId, recommentId)
                    }
                }
            }
        })
        dialog.show(requireActivity().supportFragmentManager, "CustomDialog")
    }


    private fun observeData() {
        with(viewModel) {
            result.observe(viewLifecycleOwner, Observer { board ->
                setBoard(board)
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

            errorMsg.observe(viewLifecycleOwner) {
                when (errorMsg.value) {
                    "접근할 수 없는 약속" -> {
                        makeBuilder("접근할 수 없는 약속", "마감되거나 삭제되어 접근 할 수 없는 약속입니다.")
                    }
                    "댓글이 삭제되었습니다." -> {
                        readBoard(detailBoardId)
                    }
                    "참가할 수 없는 약속" -> {
                        makeBuilder("참가할 수 없는 약속", "참가 조건이 맞지 않아 참가할 수 없는 약속입니다.")
                    }
                    "약속 참가 기능" -> {
                        when (participate) {
                            "참가하기" -> {
                                makeBuilder("약속 취소", "해당 약속을 취소했습니다.")
                            }
                            "취소하기" -> {
                                makeBuilder("약속 참가", "해당 약속에 참가되었습니다.")
                            }
                            "마감하기" -> {
                                makeBuilder("약속 마감", "해당 약속을 마감했습니다.")
                            }
                        }
                    }
                    else -> {
                        showToast(it)
                    }
                }
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun setBoard(board : Board) {
        if (board.appointmentTime != null) {
            val createDay: String = board.appointmentTime!!
            val created = createDay.split("T")
            appointmentTime = created[0] + ", " + created[1].substring(0, 5)
        }

        detailBoardId = board.id
        binding.tvCurrentMember.text = board.currentNumberOfPeople.toString()
        binding.tvTotalMember.text = board.totalNumberOfPeople.toString()
        binding.tvCurrentCommentCount.text = board.amountOfComments.toString()
        binding.tvWriteTime.text = board.createdAt
        binding.tvBoardMeetingTime.text = appointmentTime
        binding.tvBoardPlaceName.text = board.restaurantName
        binding.tvBoardTitle.text = board.title
        binding.tvBoardContent.text = board.content
        binding.tvBoardWriter.text = board.author!!.nickname

        if (board.ageRestrictionStart != null && board.ageRestrictionEnd != null) {
            val ageFilter = board.ageRestrictionStart.toString() + "부터 " + board.ageRestrictionEnd.toString() + "까지"
            binding.tvBoardAge.text = ageFilter
        }
        else {
            binding.layoutAge.visibility = View.GONE
        }

        when (board.sexRestriction) {
            "NONE" -> {
                binding.layoutGender.visibility = View.GONE
            }
            "FEMALE" -> {
                binding.tvBoardGender.text = "여성"
            }
            "MALE" -> {
                binding.tvBoardGender.text = "남성"
            }
        }

        binding.rvComment.adapter = commentAdapter
        val commentLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvComment.layoutManager = commentLayoutManager

        binding.imgPostComment.setOnClickListener {
            if (binding.etvComment.text.toString() != "" && !flag) {
                viewModel.addComment(
                    detailBoardId,
                    binding.etvComment.text.toString()
                )
                binding.etvComment.text = null
            } else if (binding.etvComment.text.toString() != "" && flag) {
                viewModel.addReComment(
                    detailBoardId,
                    detailCommentId,
                    binding.etvComment.text.toString()
                )
                binding.etvComment.text = null
                flag = false
            }
        }
        binding.rvMember.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        binding.rvMember.adapter = userAdapter

        val marker = Marker()
        marker.position = LatLng(board.latitude!!, board.longitude!!)
        marker.map = naverMap
        val cameraPosition = CameraPosition( // 카메라 위치 변경
            LatLng(board.latitude!!, board.longitude!!),  // 위치 지정
            15.0 // 줌 레벨
        )
        naverMap.cameraPosition = cameraPosition // 변경된 위치 반영

        if (board.author!!.id == App.prefs.getInt("id", -1)) {
            participate = "마감하기"
            binding.btnParticipate.text = participate
        }
        else {
            for (member in board.members!!) {
                if (member.id == App.prefs.getInt("id", -1)) {
                    participate = "취소하기"
                    break
                }
                else {
                    participate = "참가하기"
                }
            }
            binding.btnParticipate.text = participate
        }

        commentList.clear()
        if (board.comments != null) {
            for (comment in board.comments!!) {
                commentList.add(comment)
                if (comment.replies !== null) {
                    for (recomment in comment.replies!!) {
                        val reComment = Comment(
                            recomment.id,
                            recomment.author,
                            recomment.content,
                            recomment.replies,
                            typeFlag = 1,
                            createdAt = recomment.createdAt
                        )
                        commentList.add(reComment)
                    }
                }
            }
        }

        userList.clear()
        for (member in board.members!!) {
            userList.add(member)
        }
    }
}