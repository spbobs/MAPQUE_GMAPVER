package com.bobs.mapque.gmapver.map.vm

import android.location.Address
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bobs.baselibrary.base.BaseViewModel
import com.bobs.baselibrary.util.loge
import com.bobs.mapque.gmapver.map.data.IResult
import com.bobs.mapque.gmapver.map.data.searchdialog.SearchModel
import com.bobs.mapque.gmapver.searchlist.data.model.SearchItem
import com.bobs.mapque.gmapver.searchlist.data.source.SearchListDataSource
import com.bobs.mapque.gmapver.util.GpsTracker
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

class MapViewModel(private val gpsTracker: GpsTracker, private val source: SearchListDataSource) : BaseViewModel() {
    private val _ldSearchAddress: MutableLiveData<List<Address>> = MutableLiveData()
    val ldSearchAddress: LiveData<List<Address>> = _ldSearchAddress

    fun getSearchAddress(query: String, result: IResult<String>) {
        addDisposable(
            Observable.just(gpsTracker.getSearchAddress(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        if (size > 0) {
                            _ldSearchAddress.value = this
                            result.success("")
                        } else {
                            result.fail("")
                        }
                    }
                }, {
                    it.message?.let { msg ->
                        loge(msg)
                        result.fail(msg)
                    }
                })
        )
    }

    fun getAddressName(location: Location, result: IResult<String>) {
        addDisposable(
            Observable.just(gpsTracker.getAddressName(location))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        result.success(this)
                    }
                }, {
                    it.message?.let { msg ->
                        loge(msg)
                        result.fail(msg)
                    }
                })
        )
    }

    fun getMyLocation(result: IResult<Location>) {
        addDisposable(
            Observable.just(gpsTracker.getCurrentLocation())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        result.success(this)
                    } ?: result.fail()
                }, {
                    it.message?.let { msg ->
                        loge(msg)
                        result.fail(msg)
                    }
                })
        )
    }

    fun checkLocationServicesStatus(result: IResult<Boolean>){
        addDisposable(
            Observable.just(gpsTracker.checkLocationServicesStatus())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.success(it)
                }, {
                    it.message?.let { msg ->
                        loge(msg)
                        result.fail(msg)
                    }
                })
        )
    }

    fun isEnableGetLocation(out: (GpsTracker.EnabledLocationStatus) -> Unit){
        gpsTracker.isEnableGetLocation(out)
    }

    fun insertSearchAddress(query: String, address: String, latitude: Double, longitude: Double, date: DateTime){
        val serachItem = SearchItem(0, query, address, latitude, longitude, date)
        source.insertSearchItem(serachItem)
    }
}