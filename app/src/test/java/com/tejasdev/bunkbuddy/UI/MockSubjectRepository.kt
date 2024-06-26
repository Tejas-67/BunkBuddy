package com.tejasdev.bunkbuddy.UI

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.interfaces.SubjectRepositoryInterface
import kotlinx.coroutines.runBlocking
import org.junit.Rule


class MockSubjectRepository: SubjectRepositoryInterface{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val subjectItems = mutableListOf<Subject>()
    private val lectureItems = mutableListOf<Lecture>()
    val observableSubjectItems = MutableLiveData<List<Subject>>(subjectItems)
    val observableLectureItems = MutableLiveData<List<Lecture>>(lectureItems)
    private var shouldReturnNetworkError = false

    override fun getLecturesForDay(day: Int): LiveData<List<Lecture>>{
        val list = mutableListOf<Lecture>()
        for(lecture in lectureItems){
            if(lecture.dayNumber == day) list.add(lecture)
        }
        val liveData = MutableLiveData<List<Lecture>>()
        liveData.postValue(list)
        return liveData
    }

    private fun refreshLiveData(){
        observableSubjectItems.value = subjectItems
        observableLectureItems.value = lectureItems
    }

    override fun getAllSubjects(): LiveData<List<Subject>> {
        return observableSubjectItems
    }

    override suspend fun updateSubjectAndLectures(subject: Subject) {

    }

    override fun getSubjectSync(): List<Subject> {
        return observableSubjectItems.value?: listOf()
    }

    override suspend fun addSubject(subject: Subject) {
        subjectItems.add(subject)
        refreshLiveData()
    }

    override suspend fun deleteSubject(subject: Subject) {
        subjectItems.remove(subject)
        refreshLiveData()
    }

    override fun getAllLectures(): List<Lecture> {
        return lectureItems
    }

    override fun updateSubject(subject: Subject) {}

    override fun addLecture(lecture: Lecture): Int {
        lectureItems.add(lecture)
        refreshLiveData()
        return lecture.pid
    }

    override fun deleteLecture(lecture: Lecture) {
        lectureItems.remove(lecture)
        refreshLiveData()
    }

    override fun getHistory(): LiveData<List<HistoryItem>> {
        return liveData {  }
    }

    override suspend fun addHistoryItem(history: HistoryItem) {}

}