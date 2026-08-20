package com.jrprofessor.serenity.ui.screens.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrprofessor.serenity.data.model.CheckInAnalysisResult
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.data.repository.MoodRepository
import com.jrprofessor.serenity.domain.analyzer.FaceAnalysisState
import com.jrprofessor.serenity.domain.analyzer.MoodJournalAnalyzer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoodViewModel(
    private val repository: MoodRepository
) : ViewModel() {

    private val _selectedMood = MutableStateFlow<MoodType?>(MoodType.GOOD)
    val selectedMood: StateFlow<MoodType?> = _selectedMood.asStateFlow()

    private val _isFaceScanActive = MutableStateFlow(false)
    val isFaceScanActive: StateFlow<Boolean> = _isFaceScanActive.asStateFlow()

    private val _faceAnalysisState = MutableStateFlow(FaceAnalysisState())
    val faceAnalysisState: StateFlow<FaceAnalysisState> = _faceAnalysisState.asStateFlow()

    private val _journalText = MutableStateFlow("")
    val journalText: StateFlow<String> = _journalText.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisResult = MutableStateFlow<CheckInAnalysisResult?>(null)
    val analysisResult: StateFlow<CheckInAnalysisResult?> = _analysisResult.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun selectMood(mood: MoodType) {
        _selectedMood.value = mood
    }

    fun toggleFaceScan() {
        val newState = !_isFaceScanActive.value
        _isFaceScanActive.value = newState
    }

    fun updateFaceAnalysis(state: FaceAnalysisState) {
        _faceAnalysisState.value = state
        if (state.isFaceDetected) {
            // Automatically map high face confidence to appropriate mood if user is scanning
            val derivedMood = when {
                state.faceScore >= 75 -> MoodType.JOYFUL
                state.faceScore in 58..74 -> MoodType.GOOD
                state.faceScore in 45..57 -> MoodType.NEUTRAL
                state.faceScore in 35..44 -> MoodType.SAD
                else -> MoodType.OVERWHELMED
            }
            if (_isFaceScanActive.value) {
                _selectedMood.value = derivedMood
            }
        }
    }

    fun updateJournalText(text: String) {
        if (text.length <= 1000) {
            _journalText.value = text
        }
    }

    fun appendSpokenText(spoken: String) {
        if (spoken.isBlank()) return
        val current = _journalText.value
        val updated = if (current.isBlank()) spoken else "$current $spoken"
        updateJournalText(updated)
    }

    fun analyzeCheckIn(onComplete: () -> Unit) {
        val mood = _selectedMood.value ?: MoodType.NEUTRAL
        val faceScan = if (_isFaceScanActive.value && _faceAnalysisState.value.isFaceDetected) {
            _faceAnalysisState.value
        } else null

        viewModelScope.launch {
            _isAnalyzing.value = true
            // Small simulated delay for polished AI analysis UX
            delay(900)

            val result = MoodJournalAnalyzer.analyze(
                mood = mood,
                faceScore = faceScan?.faceScore,
                faceLabel = faceScan?.faceLabel,
                journalText = _journalText.value
            )

            _analysisResult.value = result
            _isAnalyzing.value = false
            onComplete()
        }
    }

    fun saveCheckIn(onSaved: () -> Unit) {
        val mood = _selectedMood.value ?: MoodType.NEUTRAL
        val analysis = _analysisResult.value ?: return

        val entry = MoodEntry(
            mood = mood,
            moodSource = if (_isFaceScanActive.value) "face_scan" else "manual",
            faceScore = if (_isFaceScanActive.value) _faceAnalysisState.value.faceScore else null,
            faceLabel = if (_isFaceScanActive.value) _faceAnalysisState.value.faceLabel else null,
            journalText = _journalText.value,
            stressLevel = analysis.stressLevel,
            themes = analysis.themes,
            suggestion = analysis.suggestion
        )

        viewModelScope.launch {
            repository.saveEntry(entry)
            _isSaved.value = true
            onSaved()
        }
    }

    fun resetCheckIn() {
        _selectedMood.value = MoodType.GOOD
        _isFaceScanActive.value = false
        _faceAnalysisState.value = FaceAnalysisState()
        _journalText.value = ""
        _analysisResult.value = null
        _isSaved.value = false
        _isAnalyzing.value = false
    }
}
