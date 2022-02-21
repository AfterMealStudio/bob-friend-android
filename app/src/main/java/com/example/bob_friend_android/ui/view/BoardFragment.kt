package com.example.bob_friend_android.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.UiThread
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
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.data.entity.Comment
import com.example.bob_friend_android.data.entity.UserItem
import com.example.bob_friend_android.databinding.FragmentBoardBinding
import com.example.bob_friend_android.ui.viewmodel.AppointmentViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class BoardFragment : BaseFragment<FragmentBoardBinding>(
    R.layout.fragment_board
), OnMapReadyCallback {
    private val viewModel by activityViewModels<AppointmentViewModel>()
    private val args : BoardFragmentArgs by navArgs()
    private var detailBoardId : Int = 0
    private var detailCommentId : Int = 0

    private val commentList : ArrayList<Comment> = ArrayList()
    private val userList : ArrayList<UserItem> = ArrayList()
    private var commentAdapter = CommentAdapter(commentList, detailBoardId)
    private val userAdapter = UserAdapter(userList)

    private lateinit var map: NaverMap

    private var participate = "참가하기"

    var appointmentTime = ""

    var inputMethodManager: InputMethodManager? = null

    var selectedId: Int? = null
    var flag: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("onon", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("onon", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("onon", "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Log.d("onon", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onon", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("onon", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("onon", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("onon", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onon", "onDestroy")
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardBinding {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
        Log.d("onon", "onCreateBinding")

        return FragmentBoardBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(requireDataBinding().tbBoard)
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

        if(args.boardId != null) {
            detailBoardId = args.boardId!!.toInt()

            val swipe = requireDataBinding().layoutSwipe
            swipe.setOnRefreshListener {
                viewModel.setAppointment(detailBoardId)
                swipe.isRefreshing = false
            }

            requireDataBinding().btnParticipate.setOnClickListener {
                if(requireDataBinding().btnParticipate.text=="마감하기"){
                    viewModel.closeAppointment(detailBoardId)
                }
                else {
                    viewModel.joinAppointment(detailBoardId)
                }
            }

            viewModel.setAppointment(detailBoardId)
        }

//        binding.backBtn.setOnClickListener {
//            onBackPressed()
//        }

        requireDataBinding().etvComment.setOnEditorActionListener { textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // 키보드 내리기
                inputMethodManager!!.hideSoftInputFromWindow(requireDataBinding().etvComment.windowToken, 0)
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
                viewModel.deleteAppointment(detailBoardId)
//                val intent = Intent().apply {
//                    putExtra("CallType", "delete")
//                }
//                setResult(RESULT_OK, intent)
//                if(!isFinishing) finish()
            }
            Menu.FIRST + 2 -> {
                viewModel.reportAppointment(detailBoardId)
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
                        requireDataBinding().rvComment[selectedId!!].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    selectedId = position
                    requireDataBinding().rvComment[position].setBackgroundColor(Color.parseColor("#DADAEC"))
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
                        viewModel.reportComment(detailBoardId, commentId, recommentId)
                    }
                }
            }

            override fun onDeleteCommentClicked() {
                if (Comment) {
                    viewModel.deleteComment(detailBoardId, commentId)
                } else {
                    if (recommentId != null) {
                        viewModel.deleteComment(detailBoardId, commentId, recommentId)
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
//                boardMap = board
            })

            errorMsg.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    when (it) {
                        "접근할 수 없는 약속" -> {
                            makeBuilder("접근할 수 없는 약속", "마감되거나 삭제되어 접근 할 수 없는 약속입니다.")
                        }
                        "댓글이 삭제되었습니다." -> {
                            setAppointment(detailBoardId)
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
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.map = naverMap

        observeData()
    }

    private fun setBoard(board : Board) {
        if (board.appointmentTime != null) {
            val createDay: String = board.appointmentTime!!
            val created = createDay.split("T")
            appointmentTime = created[0] + ", " + created[1].substring(0, 5)
        }

        detailBoardId = board.id
        requireDataBinding().tvCurrentMember.text = board.currentNumberOfPeople.toString()
        requireDataBinding().tvTotalMember.text = board.totalNumberOfPeople.toString()
        requireDataBinding().tvCurrentCommentCount.text = board.amountOfComments.toString()
        requireDataBinding().tvWriteTime.text = board.createdAt
        requireDataBinding().tvBoardMeetingTime.text = appointmentTime
        requireDataBinding().tvBoardPlaceName.text = board.restaurantName
        requireDataBinding().tvBoardTitle.text = board.title
        requireDataBinding().tvBoardContent.text = board.content
        requireDataBinding().tvBoardWriter.text = board.author!!.nickname

        if (board.ageRestrictionStart != null && board.ageRestrictionEnd != null) {
            val ageFilter = board.ageRestrictionStart.toString() + "부터 " + board.ageRestrictionEnd.toString() + "까지"
            requireDataBinding().tvBoardAge.text = ageFilter
        }
        else {
            requireDataBinding().layoutAge.visibility = View.GONE
        }

        when (board.sexRestriction) {
            "NONE" -> {
                requireDataBinding().layoutGender.visibility = View.GONE
            }
            "FEMALE" -> {
                requireDataBinding().tvBoardGender.text = "여성"
            }
            "MALE" -> {
                requireDataBinding().tvBoardGender.text = "남성"
            }
        }

        requireDataBinding().rvComment.adapter = commentAdapter
        val commentLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        requireDataBinding().rvComment.layoutManager = commentLayoutManager

        requireDataBinding().imgPostComment.setOnClickListener {
            if (requireDataBinding().etvComment.text.toString() != "" && !flag) {
                viewModel.createComment(
                    detailBoardId,
                    requireDataBinding().etvComment.text.toString()
                )
                requireDataBinding().etvComment.text = null
            } else if (requireDataBinding().etvComment.text.toString() != "" && flag) {
                viewModel.createComment(
                    detailBoardId,
                    requireDataBinding().etvComment.text.toString(),
                    detailCommentId
                )
                requireDataBinding().etvComment.text = null
                flag = false
            }
        }
        requireDataBinding().rvMember.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        requireDataBinding().rvMember.adapter = userAdapter

        if (board.author!!.id == App.prefs.getInt("id", -1)) {
            participate = "마감하기"
            requireDataBinding().btnParticipate.text = participate
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
            requireDataBinding().btnParticipate.text = participate
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

        val marker = Marker()
        marker.position = LatLng(board.latitude!!, board.longitude!!)
        marker.map = map
        val cameraPosition = CameraPosition( // 카메라 위치 변경
            LatLng(board.latitude!!, board.longitude!!),  // 위치 지정
            15.0 // 줌 레벨
        )
        map.cameraPosition = cameraPosition // 변경된 위치 반영

        userList.clear()
        for (member in board.members!!) {
            userList.add(member)
        }
    }
}