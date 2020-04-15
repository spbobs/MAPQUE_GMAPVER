package com.bobs.mapque.gmapver.searchlist.data.source

import com.bobs.mapque.gmapver.searchlist.data.model.SearchItem
import com.bobs.mapque.gmapver.searchlist.data.room.SearchItemDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class SearchListRepository(private val searchItemDao: SearchItemDao) : SearchListDataSource {
    override fun getAllSearchItems(): List<SearchItem> = searchItemDao.getAllSearchItems()

    override fun insertSearchItem(searchItem: SearchItem) {
        CoroutineScope(IO).launch {
            searchItemDao.insertSearchItem(searchItem)
        }
    }

    override fun deleteSearchItem(searchItem: SearchItem) {
        CoroutineScope(IO).launch {
            searchItemDao.deleteSearchItem(searchItem)
        }
    }
}