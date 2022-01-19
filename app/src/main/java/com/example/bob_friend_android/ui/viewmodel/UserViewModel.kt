package com.example.bob_friend_android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bob_friend_android.ui.view.base.BaseViewModel
import com.example.bob_friend_android.data.entity.DuplicatedCheck
import com.example.bob_friend_android.data.entity.User
import com.example.bob_friend_android.di.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel: BaseViewModel() {

    val TAG = "UserViewModel"

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressVisible : LiveData<Boolean>
        get() = _progressVisible

    private val _userInfo = MutableLiveData<User>()
    val userInfo : LiveData<User>
        get() = _userInfo


    fun joinUser(password : String, nickname : String, email: String, dateBirth:String, gender:String,agreeChoice:Boolean) {
            val date = "${dateBirth.substring(0,4)}-${dateBirth.substring(4,6)}-${dateBirth.substring(6)}"
            val user = HashMap<String, String>()

            user["email"] = email
            user["password"] = password
            user["nickname"] = nickname
            user["birth"] = date
            user["sex"] = gender
            user["agree"] = agreeChoice.toString()

            _progressVisible.postValue(true)
            ApiModule.apiBob.getJoinResponse(user).enqueue(object : Callback<User> {
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


    fun deleteUser(token:String, password: String) {
        val pwd = HashMap<String, String>()
        pwd["password"] = password

        _progressVisible.postValue(true)
        ApiModule.apiBob.deleteUser(token, pwd).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "join : ${response.code()}")
                when (response.code()) {
                    200 -> _msg.postValue("회원탈퇴에 성공했습니다.")
                    400 -> _msg.postValue("회원탈퇴 실패 : 비밀번호가 올바르지 않습니다.")
                    500 -> _msg.postValue("회원탈퇴 실패 : 서버 오류입니다.")
                    else -> _msg.postValue("회원탈퇴 실패: ${response.code()}")
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


    fun updateUser(email: String?, nickname: String?, password: String?,
                   sex: String?, birth: String?, agree: Boolean?) {
        val updateInfo = HashMap<String, String?>()
        if (birth!=null) {
            val dateBirth = "${birth.substring(0, 4)}-${birth.substring(4, 6)}-${birth.substring(6)}"
            updateInfo["birth"] = dateBirth
        }
        updateInfo["email"] = email
        updateInfo["nickname"] = nickname
        updateInfo["password"] = password
        updateInfo["sex"] = sex
        updateInfo["agree"] = agree.toString()

        _progressVisible.postValue(true)
        ApiModule.apiBob.updateUser(updateInfo).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d(TAG, "join : ${response.code()}")
                when (response.code()) {
                    200 -> {
                        _msg.postValue("회원정보수정에 성공했습니다.")
                        _userInfo.postValue(response.body())
                    }
                    500 -> _msg.postValue("회원정보수정 실패 : 서버 오류입니다.")
                    else -> _msg.postValue("회원정보수정 실패: ${response.code()}")
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


    fun setUserInfo() {
        ApiModule.apiBob.getUserId().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val code = response.code()
                if (code == 200) {
                    _userInfo.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다.")
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun checkUserNickname(userId: String) {
        ApiModule.apiBob.getNicknameCheck(userId).enqueue(object : Callback<DuplicatedCheck> {
            override fun onResponse(call: Call<DuplicatedCheck>, response: Response<DuplicatedCheck>) {
                Log.d(TAG, "onResponse : $response")
                if(response.body() != null){
                    val check = response.body()!!.duplicated
                    if(check){
                        _msg.postValue("이미 있는 닉네임입니다.")
                    } else if (!check){
                        _msg.postValue("사용 가능한 닉네임입니다.")
                    }
                }
                else {
                    _msg.postValue("서버와 연결을 실패했습니다.")
                }
            }
            override fun onFailure(call: Call<DuplicatedCheck>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다.")
                Log.d(TAG, "onFailure")
            }
        })
    }


    fun checkUserEmail(email: String) {
        ApiModule.apiBob.getEmailCheck(email).enqueue(object : Callback<DuplicatedCheck> {
            override fun onResponse(call: Call<DuplicatedCheck>, response: Response<DuplicatedCheck>) {
                Log.d(TAG, "onResponse : $response")
                if(response.body() != null){
                    val check = response.body()!!.duplicated
                    if(check){
                        _msg.postValue("이미 있는 이메일입니다.")
                    } else if (!check){
                        _msg.postValue("사용 가능한 이메일 입니다.")
                    }
                }
                else {
                    _msg.postValue("서버와 연결을 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<DuplicatedCheck>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다.")
                Log.d(TAG, "onFailure")
            }
        })
    }

    fun validationUpdate(password : String?, passwordCheck: String?, nickname: String?,
                   dateBirth:String?, gender:String?, nicknameCheck: Boolean): Boolean {
        if (nickname == null && password == null && passwordCheck == null
            && dateBirth == null && gender == null) {
            _msg.postValue("변경할 것이 없습니다.")
            return false
        }

        if (password != passwordCheck) {
            _msg.postValue("비밀번호가 서로 다릅니다.")
            return false
        }

        if (!nicknameCheck && nickname!=null) {
            _msg.postValue("닉네임 중복확인을 해주세요.")
            return false
        }

        return true
    }
}