package com.example.bob_friend_android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    private val _userInfo = MutableLiveData<User>()
    val userInfo : LiveData<User>
        get() = _userInfo


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
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("회원가입에 성공했습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 회원가입을 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 회원가입을 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 회원가입을 실패했습니다.")
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
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("회원탈퇴에 성공했습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 회원탈퇴에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 회원탈퇴에 실패했습니다.")
                }
//                is NetworkResponse.UnknownError -> {
//                    _msg.postValue("알 수 없는 오류 : 회원탈퇴에 실패했습니다.")
//                }
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
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("회원정보수정에 성공했습니다.")
                    _userInfo.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 회원정보수정에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 회원정보수정에 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 회원정보수정에 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun setUserInfo() {
        showProgress()
        viewModelScope.launch {
            val response = repository.setUserInfo()
            when (response) {
                is NetworkResponse.Success -> {
                    _userInfo.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 회원정보세팅에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 회원정보세팅에 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 회원정보세팅에 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun checkUserNickname(userId: String) {
        showProgress()
        viewModelScope.launch {
            val response = repository.checkUserNickname(userId)
            when (response) {
                is NetworkResponse.Success -> {
                    val check = response.body.exist
                    if(check){
                        _msg.postValue("이미 있는 닉네임입니다.")
                    } else if (!check){
                        _msg.postValue("사용 가능한 닉네임입니다.")
                    }
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 회원정보조회에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 회원정보조회에 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 회원정보조회에 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun checkUserEmail(email: String) {
        showProgress()
        viewModelScope.launch {
            val response = repository.checkUserEmail(email)
            when (response) {
                is NetworkResponse.Success -> {
                    val check = response.body.exist
                    Log.d("중복", "${response.body.exist}")
                    if (check) {
                        _msg.postValue("이미 있는 이메일입니다.")
                    } else if (!check) {
                        _msg.postValue("사용 가능한 이메일 입니다.")
                    }
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 회원정보조회에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 회원정보조회에 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 회원정보조회에 실패했습니다.")
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
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("임시 비밀번호 메일이 전송되었습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 회원정보조회에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 회원정보조회에 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 회원정보조회에 실패했습니다.")
                }
            }
            hideProgress()
        }
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