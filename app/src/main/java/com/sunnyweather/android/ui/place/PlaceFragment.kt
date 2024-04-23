package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity


class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }
    // 使用懒加载获取PlaceViewModle 实例（作者强烈推荐）

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }
    // 加载fragment_place 布局

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val layoutManager = LinearLayoutManager(activity)

        val recyclerView:RecyclerView ?= view?.findViewById(R.id.recyclerView)
        val searchPlaceEdit:EditText ?= view?.findViewById(R.id.searchPlaceEdit)
        val bgImageView:ImageView ?= view?.findViewById(R.id.bgImageView)

        recyclerView?.layoutManager = layoutManager     // 给recyclerView 设置layoutManager（LinearLayoutManager）
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView?.adapter = adapter     // 给recyclerView 设置适配器

        // 搜索框内容变化监听：addTextChangedListener
        searchPlaceEdit?.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content) // 调用PlaceViewModle 的搜索方法，进行网络请求
            } else {
                recyclerView?.visibility = View.GONE
                bgImageView?.visibility = View.VISIBLE
                viewModel.placeList.clear() // 清理搜索记录，显示背景
                adapter.notifyDataSetChanged()  // 通知adapter 刷新界面
            }
        }

        // 获取服务器的响应数据（借助LiveData 完成）
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer{ result ->
            val places = result.getOrNull()
            if (places != null) {   // 如果observe 接口传入数据不为空
                recyclerView?.visibility = View.VISIBLE
                bgImageView?.visibility = View.GONE
                viewModel.placeList.clear() // 清理原有搜索记录
                viewModel.placeList.addAll(places)  // 添加新的搜索记录
                adapter.notifyDataSetChanged()  // 通知adapter 刷新界面
            } else {    // 数据为空，弹出异常并打印异常的原因
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

}