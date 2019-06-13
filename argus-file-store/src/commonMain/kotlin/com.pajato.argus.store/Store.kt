package com.pajato.argus.store

import com.pajato.tmdb.core.Movie
import com.pajato.io.KFile

abstract class Persister {
    abstract fun create(id: String, json: String)
    abstract fun update(id: String, json: String)
    abstract fun delete(id: String)
}

fun String.toMovie(): Movie = Movie.create(this) as Movie

expect class Store(dir: String, name: String) : Persister {
    //expect val file: KFile
    val file: KFile
    //expect val repo: Map<String, Movie>
    val repo: MutableMap<String, Movie>
}

