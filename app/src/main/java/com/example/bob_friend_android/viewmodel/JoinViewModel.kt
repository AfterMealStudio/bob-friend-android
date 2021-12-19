package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressVisible : LiveData<Boolean>
        get() = _progressVisible


    fun join(password : String, passwordCheck: String, nickname : String, email: String, dateBirth:String, gender:String, agree1:Boolean, agree2:Boolean, agreeChoice:Boolean, idCheck: Boolean, emailCheck: Boolean) {
        if (validation(password, passwordCheck, nickname, email, dateBirth, gender, agree1, agree2, agreeChoice, idCheck, emailCheck)) {
            val date = "${dateBirth.substring(0,4)}-${dateBirth.substring(4,6)}-${dateBirth.substring(6)}"
            val user = HashMap<String, String>()

            user["email"] = email
            user["password"] = password
            user["nickname"] = nickname
            user["birth"] = date
            user["sex"] = gender
            user["agree"] = agreeChoice.toString()

            _progressVisible.postValue(true)
            RetrofitBuilder.apiBob.getJoinResponse(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d(TAG, "join : ${response.body()}")
                    when (response.code()) {
                        200 -> _msg.postValue("회원가입에 성공했습니다.")
                        405 -> _msg.postValue("회원가입 실패 : 아이디나 비번이 올바르지 않습니다.")
                        500 -> _msg.postValue("회원가입 실패 : 서버 오류입니다.")
                    }
                    _progressVisible.postValue(false)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다.")
                    Log.e("JoinActivity!!!", t.message.toString())
                    _progressVisible.postValue(false)
                }
            })
        }
    }


    fun deleteUser(password: String, userId: Int) {
        val pwd = HashMap<String, String>()
        pwd["password"] = password

        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.deleteUser(pwd, userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "join : ${response.body()}")
                when (response.code()) {
                    200 -> _msg.postValue("회원탈퇴에 성공했습니다.")
                    400 -> _msg.postValue("회원탈퇴 실패 : 비밀번호가 올바르지 않습니다.")
                    500 -> _msg.postValue("회원탈퇴 실패 : 서버 오류입니다.")
                }
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다.")
                Log.e("JoinActivity!!!", t.message.toString())
                _progressVisible.postValue(false)
            }
        })
    }


    fun checkUserNickname(userId: String) {
        RetrofitBuilder.apiBob.getNicknameCheck(userId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG, "onResponse : $response")
                val body = response.body()
                if(body != null){
                    if(body == true){
                        _msg.postValue("이미 있는 닉네임입니다.")
                    } else if (body == false){
                        _msg.postValue("사용 가능한 닉네임입니다.")
                    }
                }
                else {
                    _msg.postValue("서버와 연결을 실패했습니다.")
                }
            }
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다.")
                Log.d(TAG, "onFailure")
            }
        })
    }


    fun checkUserEmail(email: String) {
        RetrofitBuilder.apiBob.getEmailCheck(email).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG, "onResponse : $response")
                val check = response.body()
                if(check != null){
                    if(check == true){
                        _msg.postValue("이미 있는 이메일입니다.")
                    } else if (check == false){
                        _msg.postValue("사용 가능한 이메일 입니다.")
                    }
                }
                else {
                    _msg.postValue("서버와 연결을 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다.")
                Log.d(TAG, "onFailure")
            }
        })
    }


    private fun validation(password : String, passwordCheck: String, username: String, email:String,
                           dateBirth:String, gender:String, agree1: Boolean, agree2: Boolean, agreeChoice: Boolean, idCheck: Boolean, emailCheck: Boolean): Boolean {
        if (email.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
            _msg.postValue("아이디와 비밀번호가 비어있습니다.")
            return false
        }

        if (password != passwordCheck) {
            _msg.postValue("비밀번호가 서로 다릅니다.")
            return false
        }

        if (password.length < 6) {
            _msg.postValue("비밀번호는 6자 이상으로 해주세요.")
            return false
        }

        if (username.isEmpty()) {
            _msg.postValue("닉네임을 입력해주세요.")
            return false
        }

        if(dateBirth.length != 8) {
            _msg.postValue("생년월일의 형식이 정확하지 않습니다.")
            return false
        }

        if(gender!="MALE" && gender!="FEMALE" && gender!="THIRD") {
            _msg.postValue("성별을 지정해주세요.")
            return false
        }

        if(!idCheck) {
            _msg.postValue("아이디 중복확인을 해주세요.")
            return false
        }

        if(!emailCheck) {
            _msg.postValue("이메일 중복확인을 해주세요.")
            return false
        }

        if(!agree1) {
            _msg.postValue("이용약관을 동의 해주세요.")
            return false
        }

        if(!agree2) {
            _msg.postValue("개인정보 취급방침을 동의 해주세요.")
            return false
        }
        return true
    }
}