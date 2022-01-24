package com.example.bob_friend_android.ui.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.databinding.DialogSetOptionBinding
import com.example.bob_friend_android.ui.viewmodel.AppointmentViewModel

class SetOptionDialog(private val isWriter: Boolean, private val isComment:Boolean): DialogFragment() {

    private var _binding: DialogSetOptionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppointmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogSetOptionBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (isWriter){
            binding.reportComment.visibility = View.GONE
        }
        else {
            binding.deleteComment.visibility = View.GONE
        }

        if (!isComment){
            binding.addRecomment.visibility = View.GONE
        }

        binding.addRecomment.setOnClickListener {
            buttonClickListener.onAddReCommentClicked()
            dismiss()    // 대화상자를 닫는 함수
        }
        binding.reportComment.setOnClickListener {
            buttonClickListener.onReportCommentClicked()
            dismiss()
        }
        binding.deleteComment.setOnClickListener {
            buttonClickListener.onDeleteCommentClicked()
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 인터페이스
    interface OnButtonClickListener {
        fun onAddReCommentClicked()
        fun onReportCommentClicked()
        fun onDeleteCommentClicked()
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}