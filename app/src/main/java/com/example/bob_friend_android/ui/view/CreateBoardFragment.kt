package com.example.bob_friend_android.ui.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentBoardBinding
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentCreateBoardBinding
import com.example.bob_friend_android.ui.viewmodel.AppointmentViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class CreateBoardFragment : BaseFragment<FragmentCreateBoardBinding>(
    R.layout.fragment_create_board
), OnMapReadyCallback {
    private val viewModel by activityViewModels<AppointmentViewModel>()

    private val args : CreateBoardFragmentArgs by navArgs()
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    private var gender : String = "NONE"

    var address: String = ""
    var locationName: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var appointmentDate: String = ""
    var appointmentTime: String = ""
    var dateTime: String = ""

    var thisYear =""
    var thisMonth = ""
    var thisDay = ""
    var thisHour = ""
    var thisMinute = ""

    var ageRestrictionStart: Int? = null
    var ageRestrictionEnd: Int? = null

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateBoardBinding {

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        return FragmentCreateBoardBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        requireDataBinding().rgAge.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.rb_yes -> {
                    requireDataBinding().rsAge.visibility = View.VISIBLE
                    requireDataBinding().layoutAgeFromTo.visibility = View.VISIBLE
                }
                R.id.rb_no -> {
                    requireDataBinding().rsAge.visibility = View.GONE
                    requireDataBinding().layoutAgeFromTo.visibility = View.GONE
                }
            }
        }

        requireDataBinding().tvChoiceDate.setOnClickListener {
            hideKeyboard()
            setCalenderDay()
        }

        requireDataBinding().tvChoiceTime.setOnClickListener {
            hideKeyboard()
            setCalenderTime()
        }
        requireDataBinding().rsAge.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance(Locale.KOREAN)
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }

        requireDataBinding().rsAge.addOnChangeListener { slider, value, fromUser ->
            val time = DecimalFormat("##0")
            requireDataBinding().tvAgeFrom.text = time.format(slider.values[0])
            requireDataBinding().tvAgeTo.text = time.format(slider.values[1])
        }

        requireDataBinding().rgGender.setOnCheckedChangeListener { group, checkedId ->
            gender = when(checkedId) {
                R.id.btn_gender_male -> "MALE"
                R.id.btn_gender_female -> "FEMALE"
                R.id.btn_gender_none -> "NONE"
                else -> ""
            }
        }

        requireDataBinding().btnCreateOk.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("약속 작성하기")
            builder.setMessage("이렇게 글 작성을 진행할까요?")

            val title = requireDataBinding().etvTitle.text.toString().trim()
            val boardContent = requireDataBinding().etvContent.text.toString().trim()
            val count = requireDataBinding().etvPeopleCount.text.toString()

            if (requireDataBinding().rgAge.checkedRadioButtonId == requireDataBinding().rbYes.id) {
                ageRestrictionStart = requireDataBinding().tvAgeFrom.text.toString().toInt()
                ageRestrictionEnd = requireDataBinding().tvAgeTo.text.toString().toInt()
            }

            builder.setPositiveButton("예") { dialog, which ->
                if(validateTitle() && validateContent() && validateEtc()){
                    viewModel.createAppointment(
                        title,
                        boardContent,
                        count,
                        address,
                        locationName,
                        longitude,
                        latitude,
                        dateTime,
                        gender,
                        ageRestrictionStart,
                        ageRestrictionEnd
                    )

                    goToNext(R.id.action_createBoardFragment_self)
                }
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()

        }

        observeData()

        requireDataBinding().btnSearch.setOnClickListener {
            activity?.let{
                val editor = App.prefs.edit()
                editor.putString("title", requireDataBinding().etvTitle.text.toString())
                editor.putString("content", requireDataBinding().etvContent.text.toString())
                editor.putString("count", requireDataBinding().etvPeopleCount.text.toString())
                editor.putString("date", requireDataBinding().tvSetDate.text.toString())
                editor.putString("time", requireDataBinding().tvSetTime.text.toString())
                editor.putString("appointmentDate", appointmentDate)
                editor.putString("appointmentTime", appointmentTime)
                editor.apply()

                goToNext(R.id.action_createBoardFragment_to_searchLocationFragment)
            }
        }

        requireDataBinding().layoutCreate.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }
        }
    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireDataBinding().etvTitle.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().etvContent.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().etvPeopleCount.windowToken, 0)
    }


    private fun setCalenderDay() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateListener = object : DatePickerDialog.OnDateSetListener {
            @SuppressLint("SetTextI18n")
            override fun onDateSet(
                view: DatePicker?,
                yearDate: Int,
                monthDate: Int,
                dayOfMonth: Int
            ) {
                requireDataBinding().tvSetDate.text = "${yearDate}년 ${monthDate+1} 월 ${dayOfMonth}일"
                thisMonth = "${monthDate+1}"
                thisDay = "$dayOfMonth"

                if(thisMonth.length != 2){
                    thisMonth = "0$thisMonth"
                }

                if(thisDay.length != 2){
                    thisDay = "0$thisDay"
                }
                thisYear = "$yearDate"
                appointmentDate = "$thisYear-$thisMonth-$thisDay"
            }
        }

        val datePicker = DatePickerDialog(requireContext(), dateListener, year, month, day)
            .apply {
                datePicker.minDate = System.currentTimeMillis()
            }
        datePicker.show()
    }


    private fun setCalenderTime() {
        val time = Calendar.getInstance()
        val hour = time.get(Calendar.HOUR_OF_DAY)
        val minute = time.get(Calendar.MINUTE)

        val timeListener = object : TimePickerDialog.OnTimeSetListener{
            @SuppressLint("SetTextI18n")
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                requireDataBinding().tvSetTime.text = "${hourOfDay}시 ${minute}분"

                thisHour = "$hourOfDay"
                thisMinute = "$minute"

                if(thisHour.length != 2){
                    thisHour = "0$thisHour"
                }

                if(thisMinute.length != 2){
                    thisMinute = "0$thisMinute"
                }

                appointmentTime = "T$thisHour:$thisMinute"
            }
        }
        val builder = TimePickerDialog(requireContext(), timeListener, hour, minute, false)
        builder.show()
    }


    private fun validateTitle(): Boolean {
        val title: String = requireDataBinding().etvTitle.text.toString()

        return if (title.isEmpty()) {
            requireDataBinding().etvTitle.error = "약속 제목을 입력해주세요."
            false
        } else {
            requireDataBinding().etvTitle.error = null
            true
        }
    }


    private fun validateContent(): Boolean {
        val content: String = requireDataBinding().etvContent.text.toString()

        return if (content.isEmpty()) {
            requireDataBinding().etvContent.error = "약속 내용을 입력해주세요."
            false
        } else {
            requireDataBinding().etvContent.error = null
            true
        }
    }


    private fun validateEtc(): Boolean {
        val count: String = requireDataBinding().etvPeopleCount.text.toString()

        return if (count.isEmpty()) {
            requireDataBinding().etvPeopleCount.error = "약속 인원을 입력해주세요."
            false
        } else if (count.toInt() > 4) {
            requireDataBinding().etvPeopleCount.error = "거리두기 방침에 따라 4인 이하의 인원만 가능합니다."
            false
        } else if (count.toInt() < 2) {
            requireDataBinding().etvPeopleCount.error = "2인 미만의 글은 작성할 수 없습니다."
            false
        } else if(dateTime.length != 16) {
            showToast("약속 시간을 입력해주세요!")
            false
        } else if(locationName == "") {
            showToast("약속 장소를 입력해주세요!")
            false
        } else {
            requireDataBinding().etvPeopleCount.error = null
            true
        }
    }


    override fun onMapReady(@NonNull naverMap: NaverMap) {
        this.naverMap = naverMap
        val item = args.item

        if(item != null) {
            address = item.address.toString()
            locationName = item.name.toString()
            latitude = item.y
            longitude = item.x

            if (latitude != 0.0 && longitude != 0.0) {
                requireDataBinding().tvLocation.text = locationName
                val marker = Marker()
                marker.apply {
                    position = LatLng(latitude, longitude)
                    map = naverMap
                    val cameraPosition = CameraPosition( // 카메라 위치 변경
                        LatLng(latitude, longitude),  // 위치 지정
                        15.0 // 줌 레벨
                    )
                    naverMap.cameraPosition = cameraPosition
                }
            }

            requireDataBinding().etvTitle.setText(App.prefs.getString("title", ""))
            requireDataBinding().etvContent.setText(App.prefs.getString("content", ""))
            requireDataBinding().etvPeopleCount.setText(App.prefs.getString("count", ""))
            requireDataBinding().tvSetDate.text = App.prefs.getString("date", "")
            requireDataBinding().tvSetTime.text = App.prefs.getString("time", "")
            dateTime = "${App.prefs.getString("appointmentDate", "")}${App.prefs.getString("appointmentTime", "")}"
            Log.d("length", "${dateTime.length}")
        }
    }
}
