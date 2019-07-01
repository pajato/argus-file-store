package com.pajato.argus.store

import com.pajato.io.createKotlinFile
import com.pajato.tmdb.core.Movie
import com.pajato.tmdb.core.TmdbError
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json.Companion.stringify

fun String.getJson(): String {
    val start = this.indexOf("{")
    val end = this.lastIndexOf("}")
    if (start == -1 || end == -1) throw IllegalArgumentException("Malformed JSON; $this")
    return this.substring(start, end + 1)
}

@UnstableDefault
actual class Store actual constructor(dir: String, name: String) : Persister() {
    actual val file = createKotlinFile(dir, name)
    actual val repo = mutableMapOf<String, Movie>()

    init {
        val regex = """^(\w+) (\d+)""".toRegex()
        fun processLine(line: String) {
            fun processMatches(result: MatchResult) {
                fun createItem(key: String) {
                    fun getErrorMessage(error: TmdbError): String = "Error detected and ignored: ${error.message}!"
                    val json = line.getJson()
                    val movie = json.toMovie()
                    if (movie is TmdbError) println(getErrorMessage(movie)) else repo[key] = movie as Movie
                }
                fun removeItem(key: String) { repo.remove(key) }

                val (command, key) = result.destructured
                when (command) {
                    "create" -> createItem(key)
                    "update" -> createItem(key)
                    "delete" -> removeItem(key)
                }
            }

            regex.find(line)?.apply { processMatches(this) } ?: return
        }
        fun loadObjectsFromJournal() {
            file.readLines().map { processLine(it) }
        }
        fun rewriteJournal() {
            fun storeMovieIntoJournal(movie: Movie) {
                file.appendText("create ${movie.id} ${stringify(Movie.serializer(), movie)}\n")
            }

            file.clear()
            repo.values.map { movie -> storeMovieIntoJournal(movie) }
        }

        loadObjectsFromJournal()
        rewriteJournal()
    }

    override fun create(id: String, json: String) {
        val movie = Movie.create(json)
        if (movie is TmdbError) throw java.lang.IllegalArgumentException("Malformed JSON: $json")
        file.appendText("create $id $json\n")
        repo[id] = movie as Movie
    }

    override fun update(id: String, json: String) {
        val movie = Movie.create(json)
        if (movie is TmdbError) throw java.lang.IllegalArgumentException("Malformed JSON: $json")
        file.appendText("update $id $json\n")
        repo[id] = movie as Movie
    }

    override fun delete(id: String) {
        file.appendText("delete $id\n")
        repo.remove(id)
    }
}
