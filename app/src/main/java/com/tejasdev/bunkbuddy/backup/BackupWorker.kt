package com.tejasdev.bunkbuddy.backup

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.datamodel.DataUploadPacket
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    val authViewModel: AuthViewModel,
    val viewModel: SubjectViewModel
): CoroutineWorker(context, params){

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO){
            try{
                val subjects = viewModel.getAllSubjectSync()
                if(subjects.isEmpty()) Result.success()
                val packet = DataUploadPacket(
                    email = authViewModel.getEmail(),
                    latestSubjects = subjects
                )
                val result = suspendCoroutine { continuation->
                    authViewModel.uploadData(packet){success, message ->
                        Log.w("workmanager-backup", "$success $message")
                        continuation.resume(success to message)
                    }
                }
                if(result.first) Result.success()
                else Result.retry()
            }
            catch(e: Exception){
                Result.retry()
            }
        }
    }
}