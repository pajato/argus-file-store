package com.pajato.argus.store

import com.pajato.io.createKotlinFile
import com.pajato.tmdb.core.Movie
import com.pajato.tmdb.core.TmdbError

fun String.getJson(): String {
    val start = this.indexOf("{")
    val end = this.lastIndexOf("}")
    if (start == -1 || end == -1) throw IllegalArgumentException("Malformed JSON; $this")
    return this.substring(start, end + 1)
}

actual class Store actual constructor(dir: String, name: String) : Persister() {
    actual val file = createKotlinFile(dir, name)
    actual val repo = mutableMapOf<String, Movie>()

    init {
        val regex = """^(\w+) (\d+)""".toRegex()
        fun processLine(line: String) {
            fun processMatches(result: MatchResult) {
                fun createItem(key: String) {
                    val json = line.getJson()
                    repo[key] = json.toMovie() }
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

        // Load repository from file.
        file.readLines().map { processLine(it) }
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
