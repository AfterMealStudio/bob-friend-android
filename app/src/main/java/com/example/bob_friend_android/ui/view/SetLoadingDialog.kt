package com.example.bob_friend_android.ui.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.bob_friend_android.R
import dagger.hilt.android.AndroidEntryPoint

class SetLoadingDialog(context: Context) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_set_loading)

        // 취소 불가능
        setCancelable(false)

        // 배경 투명하게 바꿔줌
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
}