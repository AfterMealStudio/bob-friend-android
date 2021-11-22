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
import android.widget.RelativeLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentCreateBoardBinding
import com.example.bob_friend_android.view.LocationSearchActivity
import com.example.bob_friend_android.viewmodel.BoardViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class CreateBoardFragment : Fragment() {
    private val TAG = "CreateBoardFragment"
    private lateinit var binding : FragmentCreateBoardBinding
    private lateinit var viewModel : BoardViewModel
    private lateinit var  getLocationResultText: ActivityResultLauncher<Intent>

    private lateinit var mapView: MapView
    private lateinit var mapViewContainer: RelativeLayout

    private var gender : String = "NONE"

    var address: String = ""
    var locationName: String = ""
    var y: Double? = 0.0
    var x: Double? = 0.0
    var date: String = ""
    var time: String = ""

    var toast: Toast? = null


    @SuppressLint("ClickableViewAccessibility")
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

        binding.createMapView.visibility = View.VISIBLE

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
            date = setCalenderDay()
        }

        binding.createRangeSeekBar.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance(Locale.KOREAN)
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }

        binding.createRangeSeekBar.addOnChangeListener { slider, value, fromUser ->
            val time = DecimalFormat("##0")
            binding.createTimeFrom.text = time.format(slider.values[0])
            binding.createTimeTo.text = time.format(slider.values[1])
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
            val dateTime = "$date$time"

            builder.setPositiveButton("예") { dialog, which ->
                if(viewModel.validation(title, boardContent, count, locationName, dateTime)){
                    viewModel.createBoard(
                        title,
                        boardContent,
                        count,
                        address,
                        locationName,
                        x,
                        y,
                        dateTime,
                        gender
                    )
                    val ft: FragmentTransaction? = fragmentManager?.beginTransaction()
                    ft?.detach(this)?.attach(this)?.commit()
                }
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()

        }

        mapView = MapView(requireContext())
        mapView.removeAllPOIItems()
        mapViewContainer = binding.createMapView
        mapViewContainer.addView(mapView)
        mapView.setOnTouchListener { _, _ -> true }

        observeData()

        getLocationResultText = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                if(result.data != null) {
                    address = result.data?.getStringExtra("location").toString()
                    locationName = result.data?.getStringExtra("name").toString()
                    y = result.data?.getDoubleExtra("y", 0.0)
                    x = result.data?.getDoubleExtra("x", 0.0)

                    binding.createLocation.text = locationName
                    val marker = MapPOIItem()
                    marker.apply {
                        itemName = "내위치"
                        mapPoint = MapPoint.mapPointWithGeoCoord(y!!, x!!)
                        customImageResourceId = R.drawable.main_color1_marker
                        customSelectedImageResourceId = R.drawable.main_color2_marker
                        markerType = MapPOIItem.MarkerType.CustomImage
                        selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        isCustomImageAutoscale = false
                        setCustomImageAnchor(0.5f, 1.0f)
                        mapView.setMapCenterPointAndZoomLevel(mapPoint, mapView.zoomLevel, true)
                    }
                    mapView.setZoomLevel(2, false)
                    mapView.zoomIn(false)
                    mapView.zoomOut(false)
                    mapView.addPOIItem(marker)
                }
            }
        }

        binding.createSearchBtn.setOnClickListener {
            activity?.let{
                val intent = Intent(context, LocationSearchActivity::class.java)
                getLocationResultText.launch(intent)
            }
            binding.createLocation.visibility = View.VISIBLE
        }

        return binding.root
    }


    private fun setCalenderDay() : String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var isDataSet = false

        val mOnDismissListener = DialogInterface.OnDismissListener {
            if (isDataSet) {
                time = setCalenderTime()
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
            }
        }

        val datePicker = DatePickerDialog(requireContext(), dateListener, year, month, day)
            .apply {
                datePicker.minDate = System.currentTimeMillis()
        }

        datePicker.setOnDismissListener(mOnDismissListener)
        datePicker.show()



        var thisMonth = "${month+1}"
        var thisDay = "$day"

        if(thisMonth.length != 2){
            thisMonth = "0${month+1}"
        }

        if(thisDay.length != 2){
            thisDay = "0$day"
        }

        return "$year-$thisMonth-$thisDay"
    }


    override fun onResume() {
        super.onResume()
        mapView.visibility = View.VISIBLE
    }


    override fun onPause() {
        super.onPause()
        mapView.visibility = View.GONE

    }


    private fun setCalenderTime() : String {
        val time = Calendar.getInstance()
        val hour = time.get(Calendar.HOUR)
        val minute = time.get(Calendar.MINUTE)

        val timeListener = object : TimePickerDialog.OnTimeSetListener{
            @SuppressLint("SetTextI18n")
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.createTime.text = "${hourOfDay}시 ${minute}분"
            }
        }
        val builder = TimePickerDialog(requireContext(), timeListener, hour, minute, false)
        builder.show()

        var thisHour = "$hour"
        var thisMinute = "$minute"

        if(thisHour.length != 2){
            thisHour = "0$hour"
        }

        if(thisMinute.length != 2){
            thisMinute = "0$minute"
        }

        return "T$thisHour:$thisMinute"
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
}