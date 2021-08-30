package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.bob_friend_android.App
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.network.RetrofitBuilder
import com.example.bob_friend_android.network.StatusCode
import com.example.bob_friend_android.view.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class JoinViewModel(application: Application): AndroidViewModel(application) {

    val TAG = "JoinViewModel"

    fun join(userId: String, password : String, passwordCheck: String, nickname : String, email: String, dateBirth:String, gender:String, context: Context){

        if (validation(userId, password, passwordCheck, nickname, email, dateBirth, gender, context)) {
            val user = HashMap<String, String>()
            user["username"] = userId
            user["password"] = password
//            user["nickname"] = nickname
            user["email"] = email
            user["birth"] = dateBirth
            user["sex"] = gender
            Log.d(TAG, "!!!!!!!!!$user")
            RetrofitBuilder.api.getJoinResponse(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d(TAG, "response :: $response")
                    if (response.errorBody() != null) {
                        val error = response.errorBody()!!.toString().toInt()
                        if (error == StatusCode.Conflict.code) {
                            Toast.makeText(context, "이미 있는 이메일입니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        App.prefs.setString("userId", "")
                        App.prefs.setString("email", "")
                        App.prefs.setString("nickname", "")
                        App.prefs.setString("password", "")
                        App.prefs.setString("dateBirth", "")
                        App.prefs.setString("gender", "")
                        Toast.makeText(context, "회원가입 되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context.startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                    Log.e("JoinActivity!!!", t.message.toString())
                }

            })
        } else {
            return
        }


    }

    private fun validation(userId : String, password : String, passwordCheck: String, username: String, email:String, dateBirth:String, gender:String, context: Context): Boolean {

        if (userId.length == 0 || password.length == 0 || passwordCheck.length == 0) {
            Toast.makeText(context, "아이디와 비밀번호가 비어있습니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!password.equals(passwordCheck)) {
            Toast.makeText(context, "비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(context, "비밀번호는 6자 이상으로 해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (checkType(userId)) {
            Toast.makeText(context, "아이디는 영어 대문자, 소문자, 숫자로만 구성할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        if(dateBirth.length != 10) {
            Toast.makeText(context, "생년월일의 형식이 정확하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        return true
    }


    private fun checkType(word : String) : Boolean {
        return Pattern.matches("^[가-힣]*$", word)
    }
}