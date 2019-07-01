package com.pajato.argus.store

import kotlinx.serialization.UnstableDefault
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

expect fun getBuildDir(): String

@UnstableDefault
class BasicTests {

    private val dir = getBuildDir()
    private val testStoreName = "store"
    private var store = Store(dir, testStoreName)

    @Test
    fun `create update and remove a movie`() {
        store.file.clear()
        store.repo.clear()
        assertEquals(0, store.repo.size, "Repo has wrong size!")
        val errorMessage = "The store has the wrong number of lines!"
        var id = "6"
        var creationJson = """{"adult":true,"id":$id,"original_title":"Matrix","popularity":3.1,"video":true}"""
        store.create(id, creationJson)
        assertEquals(1, store.file.readLines().size, errorMessage)
        assertEquals(1, store.repo.size, "Repo has wrong size!")

        val updateJson = """{"adult":true,"id":$id,"original_title":"Maix","popularity":3.6,"video":true}"""
        store.update(id, updateJson)
        assertEquals(2, store.file.readLines().size, errorMessage)
        assertEquals(1, store.repo.size, "Repo has wrong size!")

        store.delete(id)
        assertEquals(3, store.file.readLines().size, errorMessage)
        assertEquals(0, store.repo.size, "Repo has wrong size!")

        store.create(id, creationJson)
        id = "27"
        creationJson = """{"adult":true,"id":$id,"original_title":"Matrix","popularity":3.1,"video":true}"""
        store.create("7", creationJson)
        store.file.close()
        store = Store(dir, testStoreName)
        assertEquals(2, store.file.readLines().size, "New store has incorrect number of lines!")
        assertEquals(2, store.repo.size, "New store has wrong size!")

        // Throw some exceptions.
        assertFailsWith(IllegalArgumentException::class) { store.create("abc", "") }
        id = "23"
        creationJson = """{"adult":true,"id":$id,"original_title":"Matrix","popularity":3.1,"video":true}foo"""
        assertFailsWith(IllegalArgumentException::class) { store.create(id, creationJson) }

        // Create some invalid json for coverage
        store.file.clear()
        store.file.appendText("create 23 ${creationJson}update 23 ${creationJson}")
        store.file.close()
        store = Store(dir, testStoreName)
        assertEquals(0, store.file.readLines().size, "Bad store has incorrect number of lines!")
        assertEquals(0, store.repo.size, "Bad store has wrong size!")
    }

}