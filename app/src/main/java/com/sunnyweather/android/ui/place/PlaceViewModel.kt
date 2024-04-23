package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel : ViewModel() {
    private val searchLiveData = MutableLiveData<String>()  // 将传入的搜索参数赋值给searchLiveData对象

    val placeList = ArrayList<Place>()  // 缓存数据，保证旋转手机屏幕时数据不会丢失

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }   // 使用Transformations 的switchMap() 方法观察searchLiveData对象，否则仓库层返回的LiveData 对象将无法被Activity 等进行观察（没炒熟的菜不能吃）

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }   // 当该方法调用时，switchMap() 方法所对应的转换函数就会执行。
        // 在转换函数中，我们只需要调用仓库中定义的searchPlaces() 方法就可以发起网络请求，
        // 同时将仓库层返回的LiveData 对象转换成一个可供观察Activity 的LiveData 对象

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}