package com.example.bob_friend_android.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.ui.adapter.BoardAdapter
import com.example.bob_friend_android.ui.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.FragmentSetMapBinding
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.model.Location
import com.example.bob_friend_android.data.entity.SearchLocation
import com.example.bob_friend_android.ui.viewmodel.ListViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import java.util.ArrayList


class SetMapFragment : BaseFragment<FragmentSetMapBinding>(
    R.layout.fragment_set_map
), OnMapReadyCallback, Overlay.OnClickListener {

    private val viewModel by activityViewModels<ListViewModel>()

    private lateinit var  getListResultLauncher: ActivityResultLauncher<Intent>

    private var x: Double? = null
    private var y: Double? = null
    private var placeName: String? = null
    private var click: Boolean? = false

    private var address: String = ""
    private var listPage = 0 // 현재 페이지

//    lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    //지도 검색 기능
    private val listItems = arrayListOf<SearchLocation>()   // 리사이클러 뷰 아이템
    private val searchAdapter = SearchAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드

    private val bottomViewAdapter = BoardAdapter()
    private var bottomArrayList : ArrayList<Board> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_map, container, false)

//        mapView = binding.mapFragment
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync(this)

        return binding.root
    }

    override fun init() {
        binding.rvList.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvList.adapter = searchAdapter
        binding.rvList.visibility = View.GONE

        binding.rvBottom.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvBottom.adapter = bottomViewAdapter
        binding.layoutBottom.visibility = View.GONE

        x = arguments?.getDouble("x")
        y = arguments?.getDouble("y")
        placeName = arguments?.getString("placeName")
        click = arguments?.getBoolean("click")

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.tbMap)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.etvSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyword = binding.etvSearch.text.toString()
                    pageNumber = 1
                    if(keyword!="") {
                        viewModel.searchKeywordMap(keyword)
                        binding.rvList.visibility = View.VISIBLE
                    }

                    hideKeyboard()
                    return true
                }
                return false
            }
        })

        getListResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                if(result.data != null) {
                    val callType = result.data?.getStringExtra("CallType")
                    if (callType == "delete" || callType == "close"){
                        bottomArrayList.clear()
                        viewModel.setList(listPage = listPage, type = "specific", address = address)
                    }
                }
            }
        }

        binding.rvBottom.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.rvBottom.canScrollVertically(1)) {
                    listPage++
                    viewModel.setList(listPage = listPage, type = "specific", address = address)
                }
            }
        })

        // 리스트 아이템 클릭 시 해당 위치로 이동
        searchAdapter.setItemClickListener(object : SearchAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                viewModel.setDataAtFragment(
                    this@SetMapFragment,
                    listItems[position].name!!,
                    listItems[position].y,
                    listItems[position].x
                )
                setPosition(listItems[position].y, listItems[position].x)
            }
        })

        bottomViewAdapter.setOnItemClickListener(object : BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, pos: Int) {
                activity?.let {
                    val action =
                        SetMapFragmentDirections.actionSetMapFragmentToSetBoardFragment(data.id.toString())
                    findNavController().navigate(action)
                }
            }
        })

        observeData()

        binding.layoutMap.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etvSearch.windowToken, 0)
    }

    private fun addMarkers(item: Location) {
        val marker = Marker()
        marker.position = LatLng(item.latitude, item.longitude)
        marker.map = naverMap
        marker.onClickListener = this
        marker.tag = item.address
    }


    fun setPosition(y: Double, x: Double) {
        val cameraPosition = CameraPosition(
            LatLng(y, x),
            14.0
        )
        naverMap.cameraPosition = cameraPosition
    }


    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.uiSettings.isLocationButtonEnabled = false
        naverMap.setOnMapClickListener { point, coord ->
            binding.rvBottom.visibility = View.GONE
            hideKeyboard()
            binding.rvList.visibility = View.GONE
            val cameraPositionLatitude = naverMap.cameraPosition.target.latitude
            val cameraPositionLongitude = naverMap.cameraPosition.target.longitude
            viewModel.setMarkers(10, cameraPositionLongitude, cameraPositionLatitude)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }

            searchKeyword.observe(viewLifecycleOwner) {
                if (!it?.documents.isNullOrEmpty()) {
                    listItems.clear()
                    for(document in it.documents) {
                        val item = SearchLocation(document.place_name,
                            document.road_address_name,
                            document.address_name,
                            document.x.toDouble(),
                            document.y.toDouble())

                        listItems.add(item)
                    }
                    searchAdapter.addItems(listItems)
                }
            }

            location.observe(viewLifecycleOwner) {
                for (document in it) {
                    addMarkers(document)
                }
            }

            boardList.observe(viewLifecycleOwner) {
                for(document in it.boardList) {
                    bottomArrayList.add(document)
                }
                bottomViewAdapter.addItems(bottomArrayList)
                binding.tvTotalElements.text = "약속 ${it.totalElements}개"
            }
        }
    }


    override fun onClick(p0: Overlay): Boolean {
        bottomArrayList.clear()
        address = p0.tag.toString()
        viewModel.setList(listPage = 0, type = "specific", address = address)
        binding.layoutBottom.visibility = View.VISIBLE
        return true
    }
}

