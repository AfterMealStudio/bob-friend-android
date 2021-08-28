package com.example.bob_friend_android.view


import android.app.Activity
import android.app.Instrumentation
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
import net.daum.mf.map.api.MapView

class CreateBoardActivity : AppCompatActivity() {
    private val TAG = "CreateBoardActivity2"
    private lateinit var binding : ActivityCreateBoardBinding
    private lateinit var viewModel : CreateBoardViewModel
    private lateinit var  getLocationResultText: ActivityResultLauncher<Intent>

    lateinit var mapView: MapView
    lateinit var mapViewContainer: RelativeLayout

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
            viewModel.CreateBoard(title, boardContent, this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        mapView = MapView(this)
        mapViewContainer = binding.writeMapView
        mapViewContainer.addView(mapView)
        Log.d(TAG, "onCreate: $mapView")

        getLocationResultText = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
                if(result.resultCode == RESULT_OK) {
                    val location = result.data?.getStringExtra("location")
                    val name = result.data?.getStringExtra("name")
                    val y = result.data?.getStringExtra("y")
                    val x = result.data?.getStringExtra("x")
                    val locationName = "$location $name"

                    binding.writeLocation.text = locationName
                }
            }

        binding.writeSearchBtn.setOnClickListener {
            val intent = Intent(this, BoardSearchActivity::class.java)
            getLocationResultText.launch(intent)
        }
    }


    override fun finish() {
        mapViewContainer.removeView(binding.writeMapView)
        super.finish()
    }
}