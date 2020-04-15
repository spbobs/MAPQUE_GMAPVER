package com.bobs.mapque.gmapver.map.ui

import android.content.SharedPreferences
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.bobs.baselibrary.ext.boolean
import com.bobs.baselibrary.ext.toast
import com.bobs.mapque.gmapver.R
import com.bobs.mapque.gmapver.map.data.IResult
import com.bobs.mapque.gmapver.map.data.searchdialog.SearchModel
import com.bobs.mapque.gmapver.map.vm.MapViewModel
import com.bobs.mapque.gmapver.ui.MainActivity
import com.bobs.mapque.gmapver.util.GpsTracker
import com.bobs.mapque.gmapver.util.ext.COMMON_LOCATION
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import kotlinx.android.synthetic.main.fragment_map.*
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MapFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(args: Bundle? = null): MapFragment {
            val mapFragment = MapFragment()

            if (args != null) {
                mapFragment.arguments = args
            }

            return mapFragment
        }
    }

    private var rootView: View? = null
    private var gMapView: MapView? = null
    private var gMap: GoogleMap? = null
    private val gpsTracker: GpsTracker by lazy { GpsTracker((activity as MainActivity)) }

    private val mapViewModel: MapViewModel by viewModel { parametersOf() }

    private var curlocation: Address? = null

    private var searchQuery: String? = null
    private var floatingAddress: String? = null

    val prefs: SharedPreferences by inject()
    var isFirstOpenHelpDialog by prefs.boolean("", false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gMapView = rootView?.findViewById(R.id.mapView)
        gMapView?.apply {
            onCreate(null)
            onResume()
            getMapAsync { gmap ->
                MapsInitializer.initialize(activity)

                gMap = gmap

                setMapMyLocation()
            }
        }

        // 서버에서 주소 값을 받아오면 Observer 내용이 실행된다
        mapViewModel.ldSearchAddress.observe(
            viewLifecycleOwner,
            Observer { list ->
                // 검색 다이얼로그용 리스트
                val searchModels = mutableListOf<SearchModel>()

                list.forEach {
                    searchModels.add(SearchModel(it.getAddressLine(0)))
                }

                // 검색 다이얼로그 show
                SimpleSearchDialogCompat(activity,
                    getString(R.string.search_dialog_title),
                    getString(R.string.search_dialog_search_hint),
                    null,
                    searchModels as ArrayList<SearchModel>,
                    SearchResultListener { dialog, item, position ->
                        val selectedAddress =
                            list.find { address -> address.getAddressLine(0) == item.title }

                        val addressName = selectedAddress!!.getAddressLine(0)

                        curlocation = selectedAddress

                        // y가 latitude, x가 longitude
                        moveMap(
                            LatLng(selectedAddress.latitude, selectedAddress.longitude),
                            searchQuery.toString(), addressName
                        )

                        // room에 검색한 주소 저장
                        mapViewModel.insertSearchAddress(
                            searchQuery.toString(),
                            item.title,
                            selectedAddress.latitude,
                            selectedAddress.longitude,
                            DateTime.now()
                        )

                        floatingAddress = addressName

                        dialog.dismiss()
                    }).show()
            })

        search_view.apply {
            isSubmitButtonEnabled = true

            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    showLoading()

                    query?.let {
                        mapViewModel.getSearchAddress(it, object :
                            IResult<String> {
                            override fun success(result: String) {
                                hideLoading()
                                searchQuery = it
                            }

                            override fun fail(msg: String?) {
                                hideLoading()
                                toast(msg ?: getString(R.string.search_view_search_fail))
                            }
                        })
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return true
                }
            })
        }

        helpBtn.run {
            setOnClickListener {
                showHelpDialog()
            }
        }

        myLocationBtn.run {
            setOnClickListener {
                setMapMyLocation()
            }
        }

        lastLocationBtn.run {
            setOnClickListener {
                // 마지막 검색 위치로 이동
                curlocation?.let {
                    moveMap(
                        LatLng(it.latitude, it.longitude),
                        floatingAddress.toString(),
                        it.getAddressLine(0)
                    )
                } ?: toast(getString(R.string.last_location_empty))
            }
        }

        // 앱 최초 설치 시 도움말을 띄운다
        if (!isFirstOpenHelpDialog) {
            showHelpDialog()
            isFirstOpenHelpDialog = true
        }

    }

    private fun showHelpDialog() {
        val mainActivity = activity as MainActivity

        MaterialDialog(mainActivity).show {
            title(text = getString(R.string.help_title))
            message(text = getString(R.string.help_content).trimIndent())
            positiveButton(text = getString(R.string.marker_dialog_cancelbtn_title)) {
                it.dismiss()
            }
        }
    }


    fun moveMap(latLng: LatLng, title: String, address: String) {
        val markerOptions = MarkerOptions().apply {
            position(latLng)
            title(title)
            snippet(address)
        }

        gMap?.run {
            addMarker(markerOptions)
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            animateCamera(CameraUpdateFactory.zoomTo(19f))
            setOnMarkerClickListener { marker ->
                activity?.let { activity ->
                    MaterialDialog(activity).show {
                        title(text = marker.title)
                        message(text = marker.snippet) {
                            messageTextView.gravity = Gravity.CENTER
                        }
                        positiveButton(text = getString(R.string.marker_dialog_sharebtn_title)) {
                            // 구글 좌표 공유?

                            it.dismiss()
                        }
                        negativeButton(text = getString(R.string.marker_dialog_cancelbtn_title)) {

                            it.dismiss()
                        }
                    }
                }
                true
            }

            // 지도화면일 경우 viewpager swipe를 막는다(지도의 가로,세로도 움직여야 하므로)
            setOnMapClickListener {
                resetSearchView()
            }

            setOnCameraMoveListener {
                resetSearchView()
            }
        }
    }

    private fun resetSearchView() {
        search_view.setQuery("", false)
        search_view.clearFocus()

        hideKeyboard()
    }

    fun setMapMyLocation() {
        showLoading()

        mapViewModel.isEnableGetLocation {
            // 현재 위치를 세팅한다
            mapViewModel.getMyLocation(object : IResult<Location> {
                override fun success(result: Location) {
                    hideLoading()

                    moveMap(
                        LatLng(
                            result.latitude,
                            result.longitude
                        ),
                        "내 위치",
                        gpsTracker.getAddressName(result)
                    )
                }

                override fun fail(msg: String?) {
                    toast("내 위치를 찾지 못했습니다. GPS를 확인 후 재실행 하세요.")
                }
            })
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

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun showLoading() {
        val activity = (activity as MainActivity)
        activity.showLoading()
    }

    private fun hideLoading() {
        val activity = (activity as MainActivity)
        activity.hideLoading()
    }

//    private fun showKeyboard() {
//        val activity = (activity as MainActivity)
//        activity.showKeyboard()
//    }

    private fun hideKeyboard() {
        val activity = (activity as MainActivity)
        activity.hideKeyboard()
    }

    private fun toast(msg: String) {
        val activity = (activity as MainActivity)
        activity.toast(msg)
    }
}
