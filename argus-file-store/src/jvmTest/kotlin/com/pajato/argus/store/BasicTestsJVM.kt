package com.pajato.argus.store

import com.pajato.tmdb.core.Movie
import com.pajato.tmdb.core.toTmdbData
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

actual fun getBuildDir() = "${File(BasicTests::class.java.classLoader.getResource("").path).parent}/test"

/* class BasicTestsJVM {

    private val dir = "${File(this::class.java.classLoader.getResource("").path).parent}/test"
    private val store = Store(dir, "store")

    @Test
    fun `create update and remove a movie`() {
        store.file.clear()
        store.repo.clear()
        assertEquals(0, store.repo.size, "Repo has wrong size!")
        val errorMessage = "The store has the wrong number of lines!"
        val creationJson = """{"adult":true,"id":6,"original_title":"Maix","popularity":3.1,"video":true}"""
        val movie = creationJson.toTmdbData(Movie.listName) as Movie
        store.create(movie.id.toString(10), creationJson)
        assertEquals(1, store.file.readLines().size, errorMessage)
        assertEquals(1, store.repo.size, "Repo has wrong size!")

        val updateJson = """{"adult":true,"id":6,"original_title":"Maix","popularity":3.6,"video":true}"""
        store.update(movie.id.toString(10), updateJson)
        assertEquals(2, store.file.readLines().size, errorMessage)
        assertEquals(1, store.repo.size, "Repo has wrong size!")

        store.delete(movie.id.toString())
        assertEquals(3, store.file.readLines().size, errorMessage)

        assertEquals(0, store.repo.size, "Repo has wrong size!")
    }

} */
