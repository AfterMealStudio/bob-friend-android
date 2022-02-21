package com.example.bob_friend_android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.data.entity.Event
import com.example.bob_friend_android.ui.view.base.BaseViewModel
import com.example.bob_friend_android.data.entity.User
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
): BaseViewModel() {

    private val _msg = MutableLiveData<Event<String>>()
    val errorMsg : LiveData<Event<String>> = _msg

    private val _userInfo = MutableLiveData<User>()
    val userInfo : LiveData<User> = _userInfo


    private fun postValueEvent(value : Int, type: String) {
        val msgArrayList = arrayOf("Api 오류 : $type 실패했습니다.",
            "서버 오류 : $type 실패했습니다.",
            "알 수 없는 오류 : $type 실패했습니다.",
            "${type}에 성공했습니다.",
            "이미 있는 ${type}입니다.",
            "사용 가능한 ${type}입니다."
        )

        when(value) {
            0 -> _msg.postValue(Event(msgArrayList[0]))
            1 -> _msg.postValue(Event(msgArrayList[1]))
            2 -> _msg.postValue(Event(msgArrayList[2]))
            3 -> _msg.postValue(Event(msgArrayList[3]))
            4 -> _msg.postValue(Event(msgArrayList[4]))
            5 -> _msg.postValue(Event(msgArrayList[5]))
        }
    }


    fun signUp(password : String, nickname : String, email: String, dateBirth:String, gender:String, agreeChoice:Boolean) {
        val date =
            "${dateBirth.substring(0, 4)}-${dateBirth.substring(4, 6)}-${dateBirth.substring(6)}"
        val user = HashMap<String, String>()

        user["email"] = email
        user["password"] = password
        user["nickname"] = nickname
        user["birth"] = date
        user["sex"] = gender
        user["agree"] = agreeChoice.toString()

        showProgress()
        viewModelScope.launch {
            val response = repository.signUp(user)
            val type = "회원가입에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(4, type)
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun deleteUser(token:String, password: String) {
        val pwd = HashMap<String, String>()
        pwd["password"] = password

        showProgress()
        viewModelScope.launch {
            val response = repository.deleteUser(token, pwd)
            val type = "회원탈퇴에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(4, type)
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
             hideProgress()
        }
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

        showProgress()
        viewModelScope.launch {
            val response = repository.updateUser(updateInfo)
            val type = "회원 정보수정에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(4, type)
                    _userInfo.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun setUserInfo() {
        showProgress()
        viewModelScope.launch {
            val response = repository.setUserInfo()
            val type = "회원 정보세팅에"
            when (response) {
                is NetworkResponse.Success -> {
                    _userInfo.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun checkUserNickname(userId: String) {
        showProgress()
        viewModelScope.launch {
            val response = repository.checkUserNickname(userId)
            val type = "회원 정보수정에"
            val nickname = "닉네임"
            when (response) {
                is NetworkResponse.Success -> {
                    val check = response.body.exist
                    if(check){
                        postValueEvent(4, nickname)
                    } else if (!check){
                        postValueEvent(5, nickname)
                    }
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun checkUserEmail(email: String) {
        showProgress()
        viewModelScope.launch {
            val response = repository.checkUserEmail(email)
            val type = "회원 정보조회에"
            val type2 = "이메일"
            when (response) {
                is NetworkResponse.Success -> {
                    val check = response.body.exist
                    Log.d("중복", "${response.body.exist}")
                    if(check){
                        postValueEvent(4, type2)
                    } else if (!check){
                        postValueEvent(5, type2)
                    }
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }

    fun updateUserPassword(email: String, birth: String) {
        val date =
            "${birth.substring(0, 4)}-${birth.substring(4, 6)}-${birth.substring(6)}"

        val passwordReset = HashMap<String, String>()
        passwordReset["email"] = email
        passwordReset["birth"] = date

        showProgress()
        viewModelScope.launch {
            val response = repository.updateUserPassword(passwordReset)
            val type = "회원 정보조회에"
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue(Event("임시 비밀번호 메일이 전송되었습니다."))
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }

    fun validationUpdate(nickname: String?,
                   dateBirth:String?, gender:String?, nicknameCheck: Boolean): Boolean {
        if (nickname == null && dateBirth == null && gender == null) {
            _msg.postValue(Event("변경할 것이 없습니다."))
            return false
        }

        if (!nicknameCheck && nickname!=null) {
            _msg.postValue(Event("닉네임 중복확인을 해주세요."))
            return false
        }

        return true
    }

    fun validationUpdatePassword(password : String?, passwordCheck: String?): Boolean {
        if (password != passwordCheck) {
            _msg.postValue(Event("비밀번호가 서로 다릅니다."))
            return false
        }
        return true
    }
}