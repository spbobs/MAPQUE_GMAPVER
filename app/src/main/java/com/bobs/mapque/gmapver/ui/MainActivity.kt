package com.bobs.mapque.gmapver.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.bobs.baselibrary.util.loge
import com.bobs.mapque.gmapver.map.ui.MapFragment
import com.bobs.mapque.gmapver.R
import com.bobs.mapque.gmapver.searchlist.data.model.SearchItem
import com.bobs.mapque.gmapver.searchlist.ui.SearchListFragment
import com.bobs.mapque.gmapver.ui.adapter.MainPagerAdapter
import com.bobs.mapque.gmapver.ui.dialog.NativeAdDialog
import com.bobs.mapque.gmapver.util.ADManager
import com.bobs.mapque.gmapver.util.GpsTracker
import com.bobs.mapque.util.listener.MapListener
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val GPS_ENABLE_REQUEST_CODE = 2001

    private val mapFragment = MapFragment.newInstance()
    private val btnTitles: Array<String> by lazy { resources.getStringArray(R.array.btn_titles) }
    private var pageCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if (!checkLocationServicesStatus()) {
            showDialogGpsSetting()
        } else {
            setUI()
        }
    }

    private fun setUI() {
        changeBtnText(0)

        // 우측 상단 이전 화면 버튼
        prevbtn.setOnClickListener {
            var curitem = main_viewpager.currentItem - 1

            if (curitem < 0)
                curitem = 0

            main_viewpager.currentItem = curitem

            hideKeyboard()
        }

        // 우측 상단 다음 화면 버튼
        nextbtn.setOnClickListener {
            var curitem = main_viewpager.currentItem + 1

            if (curitem > main_viewpager.childCount)
                curitem = main_viewpager.childCount - 1

            main_viewpager.currentItem = curitem

            hideKeyboard()
        }

        // 프래그먼트 페이져 어뎁터 세팅
        val mainadapter = MainPagerAdapter(
            supportFragmentManager
        ).apply {
            addFragment(mapFragment)
            addFragment(SearchListFragment.newInstance(mapListener = object :
                MapListener<SearchItem> {
                override fun moveMap(item: SearchItem) {
                    loge("db 저장 좌표: latitude: ${item.searchLatitude}, longitude: ${item.searchLongitude}")

                    showLoading()

                    mapFragment.moveMap(
                        LatLng(item.searchLatitude, item.searchLongitude),
                        item.searchQuery.toString(),
                        item.searchAddressName.toString()
                    )

                    main_viewpager.currentItem = 0
                }
            }))
        }

        pageCount = mainadapter.count

        // 뷰 페이져 세팅
        main_viewpager.apply {
            adapter = mainadapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    // state : 0 ~ 2 값을 가짐
                    // 0 : SCROLL_STATE_IDLE : 페이지 스크롤이 종료됐을때
                    // 1 : SCROLL_STATE_DRAGGING : 스크롤 중일 때
                    // 2 : SCROLL_STATE_SETTLING : 페이지가 선택되었을때
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // 스크롤 하는 동안 꾸준히 호출됨.

                }

                override fun onPageSelected(position: Int) {
                    // 스크롤 종료 후 호출됨.
                    changeBtnText(position)
                }
            })
        }

        // 로딩시 뒤 흐린 배경 용
        loading_blur.setBackgroundColor(Color.GRAY)
    }

    fun changeBtnText(position: Int) {
        when (position) {
            0 -> {
                // 처음
                prevbtn.visibility = View.GONE

                nextbtn.let {
                    it.visibility = View.VISIBLE
                    it.text = "${btnTitles[position + 1]} ${getString(R.string.btn_titles_suffix)}"
                }
            }

            pageCount - 1  -> {
                // 마지막
                prevbtn.let {
                    it.text = " ${getString(R.string.btn_titles_prefix)} ${btnTitles[position - 1]}"
                    it.visibility = View.VISIBLE
                }

                nextbtn.visibility = View.GONE
            }

            else -> {
                prevbtn.let {
                    it.text = " ${getString(R.string.btn_titles_prefix)} ${btnTitles[position - 1]}"
                    it.visibility = View.VISIBLE
                }

                nextbtn.let {
                    it.visibility = View.VISIBLE
                    it.text = "${btnTitles[position + 1]} ${getString(R.string.btn_titles_suffix)}"
                }
            }
        }
    }

    fun showDialogGpsSetting() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.gps_enable_dialog_title))
            .setMessage(
                getString(R.string.gps_enable_dialog_content).trimIndent()
            )
            .setCancelable(true)
            .setPositiveButton(getString(R.string.gps_enable_dialog_settingbtn_title)) { dialog, id ->
                val callGPSSettingIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
            }
            .setNegativeButton(
                getString(R.string.dialog_native_ad_btn_cancel)
            ) { dialog, id ->
                dialog.cancel()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            .create().show()
    }

    fun showLoading() {
        loading_blur.visibility = View.VISIBLE
        loading.visibility = View.VISIBLE
    }

    fun hideLoading() {
        if (loading.isVisible) {
            loading_blur.visibility = View.GONE
            loading.visibility = View.GONE
        }
    }

    fun showKeyboard() {
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    fun hideKeyboard() {
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            (currentFocus
                ?: View(this)).windowToken, 0
        )
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onBackPressed() {
        // 백키 터치시 네이티브 고급형 광고 다이얼로그 오픈
        NativeAdDialog(this).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE -> {
                setUI()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ADManager.unifiedNativeAd?.destroy()
    }
}
