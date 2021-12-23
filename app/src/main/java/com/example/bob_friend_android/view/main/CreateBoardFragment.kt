package com.example.bob_friend_android.view.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentCreateBoardBinding
import com.example.bob_friend_android.view.SearchLocationActivity
import com.example.bob_friend_android.viewmodel.BoardViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt


class CreateBoardFragment : Fragment(), OnMapReadyCallback {
    private val TAG = "CreateBoardFragment"
    private lateinit var binding : FragmentCreateBoardBinding
    private lateinit var viewModel : BoardViewModel
    private lateinit var  getLocationResultText: ActivityResultLauncher<Intent>

    private lateinit var mapView: MapView
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환하는 구현체
    private lateinit var naverMap: NaverMap

    private var gender : String = "NONE"

    var address: String = ""
    var locationName: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var appointmentDate: String = ""
    var appointmentTime: String = ""

    var thisYear =""
    var thisMonth = ""
    var thisDay = ""
    var thisHour = ""
    var thisMinute = ""

    var ageRestrictionStart: Int? = null
    var ageRestrictionEnd: Int? = null

    var toast: Toast? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_board,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
        binding.lifecycleOwner = this
        binding.board = this

        mapView = binding.createMapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)



        binding.createAgeGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.create_ageButton2 -> {
                    binding.createRangeSeekBar.visibility = View.VISIBLE
                    binding.createAgeFromTo.visibility = View.VISIBLE
                }
                R.id.create_ageButton1 -> {
                    binding.createRangeSeekBar.visibility = View.GONE
                    binding.createAgeFromTo.visibility = View.GONE
                }
            }
        }

        binding.createChoiceTime.setOnClickListener {
            setCalenderDay()
        }

        binding.createRangeSeekBar.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance(Locale.KOREAN)
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }

        binding.createRangeSeekBar.addOnChangeListener { slider, value, fromUser ->
            val time = DecimalFormat("##0")
            binding.createAgeFrom.text = time.format(slider.values[0])
            binding.createAgeTo.text = time.format(slider.values[1])
        }

        binding.createGenderGroup.setOnCheckedChangeListener { group, checkedId ->
            gender = when(checkedId) {
                R.id.create_genderButton -> "MALE"
                R.id.create_genderButton2 -> "FEMALE"
                R.id.create_genderButton3 -> "NONE"
                else -> ""
            }
        }

        binding.createOkBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("약속 작성하기")
            builder.setMessage("이렇게 글 작성을 진행할까요?")

            val title = binding.createEditCreateTitle.text.toString().trim()
            val boardContent = binding.createEditCreateContent.text.toString().trim()
            val count = binding.createEditPeopleCount.text.toString()
            val dateTime = "$appointmentDate$appointmentTime"

            if (binding.createAgeGroup.checkedRadioButtonId == binding.createAgeButton2.id) {
                ageRestrictionStart = binding.createAgeFrom.text.toString().toInt()
                ageRestrictionEnd = binding.createAgeTo.text.toString().toInt()
            }

            builder.setPositiveButton("예") { dialog, which ->
                if(viewModel.validation(title, boardContent, count, locationName, dateTime)){
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

                    removeFragment()
                }
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()

        }

        observeData()

        getLocationResultText = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                if(result.data != null) {
                    address = result.data?.getStringExtra("location").toString()
                    locationName = result.data?.getStringExtra("name").toString()
                    latitude = result.data!!.getDoubleExtra("latitude", 0.0)
                    longitude = result.data!!.getDoubleExtra("longitude", 0.0)

                    if (latitude != 0.0 && longitude != 0.0) {
                        binding.createLocation.text = locationName
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
                }
            }
        }

        binding.createSearchBtn.setOnClickListener {
            activity?.let{
                val intent = Intent(context, SearchLocationActivity::class.java)
                getLocationResultText.launch(intent)
            }
            binding.createLocation.visibility = View.VISIBLE
        }

        return binding.root
    }


    private fun removeFragment() {
        val fragmentC = CreateBoardFragment()
        val mFragmentManager = requireActivity().supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.nav_host_fragment, fragmentC)
        mFragmentTransaction.commit()
    }


    private fun setCalenderDay() {
        var isDataSet = false

        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        val mOnDismissListener = DialogInterface.OnDismissListener {
            if (isDataSet) {
                setCalenderTime()
            }
        }

        val dateListener = object : DatePickerDialog.OnDateSetListener {
            @SuppressLint("SetTextI18n")
            override fun onDateSet(
                view: DatePicker?,
                yearDate: Int,
                monthDate: Int,
                dayOfMonth: Int
            ) {
                isDataSet = true
                binding.createDate.text = "${yearDate}년 ${monthDate+1} 월 ${dayOfMonth}일"
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

        datePicker.setOnDismissListener(mOnDismissListener)
        datePicker.show()
    }


    private fun setCalenderTime() {
        val time = Calendar.getInstance()
        val hour = time.get(Calendar.HOUR)
        val minute = time.get(Calendar.MINUTE)

        val timeListener = object : TimePickerDialog.OnTimeSetListener{
            @SuppressLint("SetTextI18n")
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.createTime.text = "${hourOfDay}시 ${minute}분"

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


    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }
        }
    }


    override fun onMapReady(@NonNull naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
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
