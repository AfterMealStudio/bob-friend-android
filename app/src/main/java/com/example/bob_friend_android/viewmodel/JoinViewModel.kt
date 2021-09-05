package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
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
    var idCheck = false
    var emailCheck = false

    val TAG = "JoinViewModel"

    fun join(userId: String, password : String, passwordCheck: String, nickname : String, email: String, dateBirth:String, gender:String, agree:Boolean, context: Context){

        if (validation(userId, password, passwordCheck, nickname, email, dateBirth, gender, agree, context)) {
            val date = "${dateBirth.substring(0,4)}-${dateBirth.substring(4,6)}-${dateBirth.substring(6)}"
            val user = HashMap<String, String>()

            user["username"] = userId
            user["password"] = password
            user["nickname"] = nickname
            user["email"] = email
            user["birth"] = date
            user["sex"] = gender
            user["agree"] = agree.toString()
            Log.d(TAG, "!!!!!!!!!$user")

            RetrofitBuilder.api.getJoinResponse(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d(TAG, "response :: $response")
                    when (response.code()) {
                        200 -> {
                            Toast.makeText(context, "회원가입 성공", Toast.LENGTH_LONG).show()
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            context.startActivity(intent)
                        }
                        405 -> Toast.makeText(context, "회원가입 실패 : 아이디나 비번이 올바르지 않습니다", Toast.LENGTH_LONG).show()
                        500 -> Toast.makeText(context, "회원가입 실패 : 서버 오류", Toast.LENGTH_LONG).show()
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

    private fun validation(userId : String, password : String, passwordCheck: String, username: String, email:String, dateBirth:String, gender:String, agree: Boolean, context: Context): Boolean {
        if (userId.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
            Toast.makeText(context, "아이디와 비밀번호가 비어있습니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != passwordCheck) {
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

        if (username.isEmpty()) {
            Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        if(dateBirth.length != 8) {
            Toast.makeText(context, "생년월일의 형식이 정확하지 않습니다.", Toast.LENGTH_SHORT).show()
        }

        if(gender!="MALE" && gender!="FEMALE" && gender!="THIRD") {
            Toast.makeText(context, "성별을 지정해주세요.", Toast.LENGTH_SHORT).show()
        }

        if(!idCheck) {
            Toast.makeText(context, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
        }

        return true
    }


    private fun checkType(word : String) : Boolean {
        return Pattern.matches("^[가-힣]*$", word)
    }


    fun checkUserId(userId: String, context: Context) {
        RetrofitBuilder.api.getIdCheck(userId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG, "onResponse : $response")
                val body = response.body()
                if(body != null){
                    if(body == true){
                        Toast.makeText(context, "이미 있는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    } else if (body == false){
                        Toast.makeText(context, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
                        idCheck = true
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, "onFailure")
            }
        })
    }


    fun checkUserEmail(email: String, context: Context) {
        RetrofitBuilder.api.getIdCheck(email).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG, "onResponse : $response")
                val check = response.body()

                if(check != null){
                    if(check == true){
                        Toast.makeText(context, "이미 있는 이메일입니다.", Toast.LENGTH_SHORT).show()
                    } else if (check == false){
                        Toast.makeText(context, "사용 가능한 이메일 입니다.", Toast.LENGTH_SHORT).show()
                        emailCheck = true
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, "onFailure")
            }
        })
    }
}