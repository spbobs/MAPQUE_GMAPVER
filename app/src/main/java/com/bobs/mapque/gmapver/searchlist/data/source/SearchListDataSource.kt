package com.bobs.mapque.gmapver.searchlist.data.source

import com.bobs.mapque.gmapver.searchlist.data.model.SearchItem

interface SearchListDataSource {
    fun getAllSearchItems() : List<SearchItem>

    fun insertSearchItem(searchItem: SearchItem)

    fun deleteSearchItem(searchItem: SearchItem)
}