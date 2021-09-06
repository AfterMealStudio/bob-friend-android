package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.bob_friend_android.App
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.network.RetrofitBuilder
import com.example.bob_friend_android.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel(application: Application): AndroidViewModel(application) {

    val TAG = "CreateBoardViewModel"

    fun CreateBoard(title : String, content: String, count:String, address: String, locationName: String, x: Double?, y: Double?, time: String, context: Context) {
        if(validation(title,content,count,address, locationName, x, y, time, context)) {
            val board = Board(
                title = title,
                content = content,
                totalNumberOfPeople = count.toInt(),
                restaurantAddress = address,
                restaurantName = locationName,
                longitude = y,
                latitude = x,
                appointmentTime = time
            )
            val token = App.prefs.getString("token", "no token")

            Log.d(TAG, "!title=$title, content=$content")

            if (token != null) {
                RetrofitBuilder.api.addRecruitmens(token, board).enqueue(object : Callback<Board> {
                    override fun onResponse(call: Call<Board>, response: Response<Board>) {
                        val code = response.code()
                        if (code == 200) {
                            Log.d(TAG, "!!title=$title, content=$content")
                            Toast.makeText(context, "저장되었습니다!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Board>, t: Throwable) {
                        Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("AddViewModel!!!", t.message.toString())
                    }

                })
            }
        }
    }


    fun delete(context: Context, id : Int) {
        val token = App.prefs.getString("token", "no token")

        if (token != null) {
            RetrofitBuilder.api.deleteRecruitmens(token, id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                    Log.e("DetailActivity!!!", t.message.toString())
                }
            })
        }
    }


     fun validation(title : String, content: String, count:String, address: String, locationName: String, x: Double?, y: Double?, time: String, context: Context): Boolean {
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(context, "약속 제목과 내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (count == "0" || count.toString()=="") {
            Toast.makeText(context, "약속 인원의 수를 입력해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (locationName.isEmpty()) {
            Toast.makeText(context, "약속 장소를 입력해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (time.isEmpty()) {
            Toast.makeText(context, "약속 시간을 입력해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}