package com.example.bob_friend_android.view


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityCreateBoardBinding
import com.example.bob_friend_android.viewmodel.CreateBoardViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class CreateBoardActivity : AppCompatActivity() {
    private val TAG = "CreateBoardActivity"
    private lateinit var binding : ActivityCreateBoardBinding
    private lateinit var viewModel : CreateBoardViewModel
    private lateinit var  getLocationResultText: ActivityResultLauncher<Intent>

    private lateinit var mapView: MapView
    private lateinit var mapViewContainer: RelativeLayout

    var address: String = ""
    var name: String = ""
    var y: Double? = 0.0
    var x: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_board)
        viewModel = ViewModelProvider(this).get(CreateBoardViewModel::class.java)
        binding.create = this
        binding.lifecycleOwner = this

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.ageDetail.visibility = View.GONE
        binding.ageGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.ageButton2 -> {
                    binding.ageDetail.visibility = View.VISIBLE
                }
                R.id.ageButton1 -> binding.ageDetail.visibility = View.GONE
            }
        }

        binding.calendarView.visibility = View.GONE
        binding.dayGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.todayBtn -> binding.calendarView.visibility = View.GONE
                R.id.notTodayBtn -> binding.calendarView.visibility = View.VISIBLE
            }
        }

        binding.writeOkBtn.setOnClickListener {
            val title = binding.editCreateTitle.text.toString().trim()
            val boardContent = binding.editCreateContent.text.toString().trim()
            val count = binding.editPeopleCount.text.toString().toInt()
            val location = binding.writeLocation.text.toString().trim()
            val gender = binding.editCreateTitle.text.toString().trim()
            val age = binding.editCreateContent.text.toString().trim()
            val time = binding.editCreateContent.text.toString().trim()

            viewModel.CreateBoard(title, boardContent, count, address, name, this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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
                    address = result.data?.getStringExtra("location").toString()
                    name = result.data?.getStringExtra("name").toString()
                    y = result.data?.getDoubleExtra("y",0.0)
                    x = result.data?.getDoubleExtra("x",0.0)

                    binding.writeLocation.text = name
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

        binding.writeSearchBtn.setOnClickListener {
            val intent = Intent(this, BoardSearchActivity::class.java)
            getLocationResultText.launch(intent)
        }
    }


    override fun finish() {
        binding.writeMapView.visibility = View.GONE
        super.finish()
    }
}