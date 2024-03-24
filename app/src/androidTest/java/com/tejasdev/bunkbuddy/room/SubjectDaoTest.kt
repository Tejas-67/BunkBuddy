package com.tejasdev.bunkbuddy.room


import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.getOrAwaitValue
import com.tejasdev.bunkbuddy.room.dao.SubjectDao
import com.tejasdev.bunkbuddy.room.db.SubjectDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class SubjectDaoTest {

//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: SubjectDatabase
    private lateinit var dao: SubjectDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                SubjectDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        dao = database.getDao()
    }
    @After
    fun destroyEverything(){
        database.close()
    }


    @Test
    fun insertShoppingItem() = runBlocking {
        val subject = Subject(
            "test_subject",
            1,
             0,
            1,
            "now",
            75
        )
        dao.addSubject(subject)
        val allShoppingItems = dao.getAllSubjects().getOrAwaitValue()
        Log.w("testing-dao", allShoppingItems.toString())
        assertThat(allShoppingItems).contains(subject)
    }

    @Test
    fun deleteShoppingItem() = runBlocking {
        val subject = Subject(
            "test_subject",
            1,
            0,
            1,
            "now",
            75
        )
        dao.addSubject(subject)
        dao.deleteSubject(subject)
        val allItems = dao.getAllSubjects().getOrAwaitValue()
        assertThat(allItems).doesNotContain(subject)
    }

    @
}