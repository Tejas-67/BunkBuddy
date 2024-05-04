package com.tejasdev.bunkbuddy.datamodel

data class DataUploadPacket(
    val email: String,
    val latestSubjects: List<Subject>
)