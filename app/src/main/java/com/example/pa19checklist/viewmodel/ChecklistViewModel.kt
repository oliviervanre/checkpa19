package com.example.pa19checklist.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pa19checklist.data.ChecklistJsonLoader
import com.example.pa19checklist.data.SessionStorage
import com.example.pa19checklist.data.model.Aircraft
import com.example.pa19checklist.data.model.Phase
import com.example.pa19checklist.data.model.SessionState

data class ChecklistUiState(
    val isLoading: Boolean = true,
    val aircraft: Aircraft? = null,
    val sessionState: SessionState = SessionState(),
    val isPaused: Boolean = false,
    val showStartMemo: Boolean = false,
    val showShutdownChecklist: Boolean = false,
    val shutdownItemIndex: Int = 0,
    val errorMessage: String? = null
) {
    val currentPhase: Phase?
        get() = aircraft?.phases?.getOrNull(sessionState.currentPhaseIndex)

    val currentItemIndex: Int
        get() = sessionState.currentItemIndex

    val phaseCount: Int
        get() = aircraft?.phases?.size ?: 0

    val phaseNumber: Int
        get() = (sessionState.currentPhaseIndex + 1).coerceAtMost(phaseCount)

    val isCompleted: Boolean
        get() {
            val phase = currentPhase ?: return false
            return sessionState.currentPhaseIndex == phaseCount - 1 &&
                sessionState.currentItemIndex >= phase.items.size
        }

    val isShutdownCompleted: Boolean
        get() = shutdownItemIndex >= shutdownChecklistItems.size
}

val shutdownChecklistItems = listOf(
    "Roue Arrière Alignée",
    "Régime Moteur 1000 Tr / Mn",
    "Annonce Effectuée\nArrêt Radio",
    "Transpondeur Arrêt 7000",
    "Balise de détresse Balise silencieuse",
    "Magnétos Essai coupure",
    "Génératrice électrique Arrêt",
    "Mélange Etouffoir",
    "Contact Magnétos Sur OFF",
    "Anticoll / Feux de Nav. Arrêt / Coupés",
    "Batterie Arrêt",
    "Horamètre Noté"
)

class ChecklistViewModel(
    application: Application
) : AndroidViewModel(application) {

    val uiState: MutableState<ChecklistUiState> = mutableStateOf(ChecklistUiState())

    private val jsonLoader = ChecklistJsonLoader(application)
    private val sessionStorage = SessionStorage(application)

    init {
        loadChecklist()
    }

    fun validateCurrentItem() {
        val state = uiState.value
        val aircraft = state.aircraft ?: return
        val currentPhase = state.currentPhase ?: return

        if (state.isCompleted) return

        if (state.sessionState.currentPhaseIndex == aircraft.phases.lastIndex) {
            val completionState = state.sessionState.copy(currentItemIndex = currentPhase.items.size)
            persistSession(completionState)
            return
        }

        val nextSessionState = if (state.sessionState.currentItemIndex < currentPhase.items.lastIndex) {
            state.sessionState.copy(currentItemIndex = state.sessionState.currentItemIndex + 1)
        } else if (state.sessionState.currentPhaseIndex < aircraft.phases.lastIndex) {
            SessionState(
                currentPhaseIndex = state.sessionState.currentPhaseIndex + 1,
                currentItemIndex = 0
            )
        } else {
            state.sessionState.copy(currentItemIndex = currentPhase.items.size)
        }

        persistSession(nextSessionState)
    }

    fun togglePause() {
        uiState.value = uiState.value.copy(isPaused = !uiState.value.isPaused)
    }

    fun dismissStartMemo() {
        uiState.value = uiState.value.copy(showStartMemo = false)
    }

    fun showShutdownChecklist() {
        uiState.value = uiState.value.copy(
            showShutdownChecklist = true,
            shutdownItemIndex = 0
        )
    }

    fun validateShutdownItem() {
        val state = uiState.value
        if (!state.showShutdownChecklist || state.isShutdownCompleted) return

        uiState.value = state.copy(
            shutdownItemIndex = (state.shutdownItemIndex + 1).coerceAtMost(shutdownChecklistItems.size)
        )
    }

    fun resetSession() {
        if (uiState.value.aircraft == null) {
            loadChecklist()
            return
        }

        sessionStorage.clear()
        val safeState = normalizedSession(SessionState(), uiState.value.aircraft)
        sessionStorage.save(safeState)
        uiState.value = uiState.value.copy(
            sessionState = safeState,
            isPaused = false,
            showStartMemo = true,
            showShutdownChecklist = false,
            shutdownItemIndex = 0,
            errorMessage = null
        )
    }

    fun finishChecklist() {
        resetSession()
    }

    private fun loadChecklist() {
        runCatching {
            val aircraft = jsonLoader.loadAircraft()
            val restoredSession = normalizedSession(sessionStorage.load(), aircraft)
            sessionStorage.save(restoredSession)
            uiState.value = ChecklistUiState(
                isLoading = false,
                aircraft = aircraft,
                sessionState = restoredSession,
                isPaused = false,
                showStartMemo = restoredSession.currentPhaseIndex == 0 &&
                    restoredSession.currentItemIndex == 0,
                showShutdownChecklist = false,
                shutdownItemIndex = 0,
                errorMessage = null
            )
        }.onFailure {
            uiState.value = ChecklistUiState(
                isLoading = false,
                errorMessage = "Impossible de charger la checklist."
            )
        }
    }

    private fun persistSession(sessionState: SessionState) {
        sessionStorage.save(sessionState)
        uiState.value = uiState.value.copy(sessionState = sessionState)
    }

    private fun normalizedSession(sessionState: SessionState, aircraft: Aircraft?): SessionState {
        val phases = aircraft?.phases.orEmpty()
        if (phases.isEmpty()) return SessionState()

        val safePhaseIndex = sessionState.currentPhaseIndex.coerceIn(0, phases.lastIndex)
        val itemCount = phases[safePhaseIndex].items.size
        val safeItemIndex = sessionState.currentItemIndex.coerceIn(0, itemCount)

        return SessionState(
            currentPhaseIndex = safePhaseIndex,
            currentItemIndex = safeItemIndex
        )
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChecklistViewModel(application) as T
        }
    }
}
