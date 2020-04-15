package com.bobs.mapque.gmapver.util.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bobs.mapque.gmapver.searchlist.data.model.SearchItem
import com.bobs.mapque.gmapver.searchlist.ui.SearchListAdapter

@BindingAdapter("bind_items")
fun setItems(view: RecyclerView, items: List<SearchItem>?){
    items?.let {
        val adapter = view.adapter as SearchListAdapter
        adapter.items = it
    }
}