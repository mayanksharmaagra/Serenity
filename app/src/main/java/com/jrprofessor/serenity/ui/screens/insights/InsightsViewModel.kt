package com.jrprofessor.serenity.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.data.repository.MoodRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModel(
    private val repository: MoodRepository
) : ViewModel() {

    private val _selectedDaysFilter = MutableStateFlow(30)
    val selectedDaysFilter: StateFlow<Int> = _selectedDaysFilter.asStateFlow()

    val allEntries: StateFlow<List<MoodEntry>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredEntries: StateFlow<List<MoodEntry>> = _selectedDaysFilter
        .flatMapLatest { days ->
            repository.getEntriesForDays(days)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStreak: StateFlow<Int> = allEntries
        .map { list -> repository.calculateStreak(list) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    fun setDaysFilter(days: Int) {
        _selectedDaysFilter.value = days
    }

    fun deleteEntry(entry: MoodEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}
