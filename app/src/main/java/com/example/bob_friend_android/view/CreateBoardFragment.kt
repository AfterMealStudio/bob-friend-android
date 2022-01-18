package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
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
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentCreateBoardBinding
import com.example.bob_friend_android.viewmodel.BoardViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class CreateBoardFragment : BaseFragment<FragmentCreateBoardBinding>(
    R.layout.fragment_create_board
), OnMapReadyCallback {
    private val viewModel by activityViewModels<BoardViewModel>()

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_board, container, false)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return binding.root
    }

    override fun init() {
        binding.rgAge.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.rb_yes -> {
                    binding.rsAge.visibility = View.VISIBLE
                    binding.layoutAgeFromTo.visibility = View.VISIBLE
                }
                R.id.rb_no -> {
                    binding.rsAge.visibility = View.GONE
                    binding.layoutAgeFromTo.visibility = View.GONE
                }
            }
        }

        binding.tvChoiceDate.setOnClickListener {
            hideKeyboard()
            setCalenderDay()
        }

        binding.tvChoiceTime.setOnClickListener {
            hideKeyboard()
            setCalenderTime()
        }
        binding.rsAge.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance(Locale.KOREAN)
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }

        binding.rsAge.addOnChangeListener { slider, value, fromUser ->
            val time = DecimalFormat("##0")
            binding.tvAgeFrom.text = time.format(slider.values[0])
            binding.tvAgeTo.text = time.format(slider.values[1])
        }

        binding.rgGender.setOnCheckedChangeListener { group, checkedId ->
            gender = when(checkedId) {
                R.id.btn_gender_male -> "MALE"
                R.id.btn_gender_female -> "FEMALE"
                R.id.btn_gender_none -> "NONE"
                else -> ""
            }
        }

        binding.btnCreateOk.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("약속 작성하기")
            builder.setMessage("이렇게 글 작성을 진행할까요?")

            val title = binding.etvTitle.text.toString().trim()
            val boardContent = binding.etvContent.text.toString().trim()
            val count = binding.etvPeopleCount.text.toString()
            dateTime = "$appointmentDate$appointmentTime"

            if (binding.rgAge.checkedRadioButtonId == binding.rbYes.id) {
                ageRestrictionStart = binding.tvAgeFrom.text.toString().toInt()
                ageRestrictionEnd = binding.tvAgeTo.text.toString().toInt()
            }

            builder.setPositiveButton("예") { dialog, which ->
                if(validateTitle() && validateContent() && validateCount() && viewModel.validation(locationName, dateTime)){
                    viewModel.createBoard(
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
                }
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()

        }

        observeData()

        binding.btnSearch.setOnClickListener {
            activity?.let{
                val editor = App.prefs.edit()
                editor.putString("title", binding.etvTitle.text.toString())
                editor.putString("content", binding.etvContent.text.toString())
                editor.putString("count", binding.etvPeopleCount.text.toString())
                editor.putString("date", binding.tvSetDate.text.toString())
                editor.putString("time", binding.tvSetTime.text.toString())
                editor.putString("appointmentDate", appointmentDate)
                editor.putString("appointmentTime", appointmentTime)
                editor.apply()

                goToNext(R.id.action_createBoardFragment_to_searchLocationFragment)
            }
        }

        binding.layoutCreate.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                if(it == "약속 작성 성공") {
                    showToast("약속이 작성되었습니다!")
                    goToNext(R.id.action_createBoardFragment_self)
                }
                else {
                    showToast(it)
                }
            }
        }
    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etvTitle.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.etvContent.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.etvPeopleCount.windowToken, 0)
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
                binding.tvSetDate.text = "${yearDate}년 ${monthDate+1} 월 ${dayOfMonth}일"
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
                binding.tvSetTime.text = "${hourOfDay}시 ${minute}분"

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
        val title: String = binding.etvTitle.text.toString()

        return if (title.isEmpty()) {
            binding.etvTitle.error = "약속 제목을 입력해주세요."
            false
        } else {
            binding.etvTitle.error = null
            true
        }
    }


    private fun validateContent(): Boolean {
        val content: String = binding.etvContent.text.toString()

        return if (content.isEmpty()) {
            binding.etvContent.error = "약속 내용을 입력해주세요."
            false
        } else {
            binding.etvContent.error = null
            true
        }
    }


    private fun validateCount(): Boolean {
        val count: String = binding.etvPeopleCount.text.toString()

        return if (count.isEmpty()) {
            binding.etvPeopleCount.error = "약속 인원을 입력해주세요."
            false
        } else if (count.toInt() > 4) {
            binding.etvPeopleCount.error = "거리두기 방침에 따라 4인 이하의 인원만 가능합니다."
            false
        } else if (count.toInt() < 2) {
            binding.etvPeopleCount.error = "2인 이하의 글은 작성할 수 없습니다."
            false
        } else {
            binding.etvPeopleCount.error = null
            true
        }
    }


    override fun onMapReady(@NonNull naverMap: NaverMap) {
        this.naverMap = naverMap

        val args : CreateBoardFragmentArgs by navArgs()
        val item = args.item

        if(item != null) {
            address = item.address.toString()
            locationName = item.name.toString()
            latitude = item.y
            longitude = item.x

            if (latitude != 0.0 && longitude != 0.0) {
                binding.tvLocation.text = locationName
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

            binding.etvTitle.setText(App.prefs.getString("title", ""))
            binding.etvContent.setText(App.prefs.getString("content", ""))
            binding.etvPeopleCount.setText(App.prefs.getString("count", ""))
            binding.tvSetDate.text = App.prefs.getString("date", "")
            binding.tvSetTime.text = App.prefs.getString("time", "")
            dateTime = "${App.prefs.getString("appointmentDate", "")}${App.prefs.getString("appointmentTime", "")}"
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
