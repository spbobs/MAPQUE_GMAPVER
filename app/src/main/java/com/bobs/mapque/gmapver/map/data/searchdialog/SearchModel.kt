package com.bobs.mapque.gmapver.map.data.searchdialog

import ir.mirrajabi.searchdialog.core.Searchable

class SearchModel(private val title: String) : Searchable {
    override fun getTitle(): String = title
}