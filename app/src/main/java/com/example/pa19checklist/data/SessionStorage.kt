package com.example.pa19checklist.data

import android.content.Context
import com.example.pa19checklist.data.model.SessionState

class SessionStorage(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): SessionState {
        return SessionState(
            currentPhaseIndex = preferences.getInt(KEY_PHASE_INDEX, 0),
            currentItemIndex = preferences.getInt(KEY_ITEM_INDEX, 0)
        )
    }

    fun save(state: SessionState) {
        preferences.edit()
            .putInt(KEY_PHASE_INDEX, state.currentPhaseIndex)
            .putInt(KEY_ITEM_INDEX, state.currentItemIndex)
            .apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "checklist_session"
        const val KEY_PHASE_INDEX = "current_phase_index"
        const val KEY_ITEM_INDEX = "current_item_index"
    }
}
