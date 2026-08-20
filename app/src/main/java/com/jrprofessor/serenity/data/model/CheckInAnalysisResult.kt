package com.jrprofessor.serenity.data.model

data class CheckInAnalysisResult(
    val stressLevel: Int, // 0-100
    val calmLevel: Int, // 100 - stressLevel
    val themes: List<String>,
    val suggestion: String,
    val summarySentence: String
)
