package com.example.bob_friend_android.view


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityCreateBoardBinding
import com.example.bob_friend_android.viewmodel.BoardViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class CreateBoardActivity : AppCompatActivity() {
    private val TAG = "CreateBoardActivity"
    private lateinit var binding : ActivityCreateBoardBinding
    private lateinit var viewModel : BoardViewModel
    private lateinit var  getLocationResultText: ActivityResultLauncher<Intent>

    private lateinit var mapView: MapView
    private lateinit var mapViewContainer: RelativeLayout

    private var backKeyPressedTime : Long = 0

    var address: String = ""
    var locationName: String = ""
    var y: Double? = 0.0
    var x: Double? = 0.0
    var date: String = ""
    var time: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_board)
        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
        binding.create = this
        binding.lifecycleOwner = this

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.rangeSeekBar.visibility = View.INVISIBLE
        binding.ageFromTo.visibility = View.GONE
        binding.ageGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.ageButton2 -> {
                    binding.rangeSeekBar.visibility = View.VISIBLE
                    binding.ageFromTo.visibility = View.VISIBLE
                }
                R.id.ageButton1 -> {
                    binding.rangeSeekBar.visibility = View.INVISIBLE
                    binding.ageFromTo.visibility = View.GONE
                }
            }
        }

        binding.writeChoiceTime.setOnClickListener {
            time = setCalenderTime()
            date = setCalenderDay()
        }

        binding.rangeSeekBar.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance(Locale.KOREAN)
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }

        binding.rangeSeekBar.addOnChangeListener { slider, value, fromUser ->
            val time = DecimalFormat("##0")
            binding.writeTimeFrom.text = time.format(slider.values[0])
            binding.writeTimeTo.text = time.format(slider.values[1])
        }

        binding.writeOkBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("약속 작성하기")
            builder.setMessage("이렇게 글 작성을 진행할까요?")

            val title = binding.editCreateTitle.text.toString().trim()
            val boardContent = binding.editCreateContent.text.toString().trim()
            val count = binding.editPeopleCount.text.toString()
            val gender = binding.editCreateTitle.text.toString().trim()
            val age = binding.editCreateContent.text.toString().trim()
            val dateTime = "$date$time"

            builder.setPositiveButton("예") { dialog, which ->
                if(viewModel.validation(title, boardContent, count, address, locationName, x, y, dateTime, this)){
                    viewModel.CreateBoard(title, boardContent, count, address, locationName, x, y, dateTime, this)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()
        }

        mapView = MapView(this)
        mapView.removeAllPOIItems()

        mapViewContainer = binding.writeMapView
        mapViewContainer.addView(mapView)
        Log.d(TAG, "onCreate: $mapView")


        getLocationResultText = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
                if(result.resultCode == RESULT_OK) {
                    if(result.data != null) {
                        address = result.data?.getStringExtra("location").toString()
                        locationName = result.data?.getStringExtra("name").toString()
                        y = result.data?.getDoubleExtra("y",0.0)
                        x = result.data?.getDoubleExtra("x",0.0)

                        binding.writeLocation.text = locationName
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
                        mapView.addPOIItem(marker)
                    }
                }
            }

        binding.writeLocation.visibility = View.GONE
        binding.writeSearchBtn.setOnClickListener {
            val intent = Intent(this, LocationSearchActivity::class.java)
            getLocationResultText.launch(intent)
            binding.writeLocation.visibility = View.VISIBLE
        }
    }


    private fun setCalenderDay() : String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                binding.writeDate.text = "$year 년 ${month+1} 월 $dayOfMonth 일"
            }
        }

        val builder = DatePickerDialog(this, dateListener, year, month, day)
        builder.show()

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

    private fun setCalenderTime() : String {
        val time = Calendar.getInstance()
        val hour = time.get(Calendar.HOUR)
        val minute = time.get(Calendar.MINUTE)

        val timeListener = object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.writeTime.text = "$hourOfDay 시 $minute 분"
            }
        }
        val builder = TimePickerDialog(this, timeListener, hour, minute, false)
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


    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 4000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 4000) {
            super.onBackPressed()
        }
    }


    override fun finish() {
        binding.writeMapView.visibility = View.GONE
        super.finish()
    }


}