package com.bobs.mapque.gmapver.map.data

interface IResult<T> {
    fun success(result: T)
    fun fail(msg: String? = null)
}