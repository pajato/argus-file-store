package com.pajato.argus.store

import com.pajato.tmdb.core.Movie
import com.pajato.io.KFile
import com.pajato.tmdb.core.TmdbData
import com.pajato.tmdb.core.TmdbError

abstract class Persister {
    abstract fun create(id: String, json: String)
    abstract fun update(id: String, json: String)
    abstract fun delete(id: String)
}

fun String.toMovie(): TmdbData = try { Movie.create(this) } catch (exc: Exception) { TmdbError("Invalid JSON: $this") }

expect class Store(dir: String, name: String) : Persister {
    //expect val file: KFile
    val file: KFile
    //expect val repo: Map<String, Movie>
    val repo: MutableMap<String, Movie>
}

