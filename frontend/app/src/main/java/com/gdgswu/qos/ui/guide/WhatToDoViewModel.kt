package com.gdgswu.qos.ui.guide

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel

class WhatToDoViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("qos_prefs", Context.MODE_PRIVATE)

    /** Completed mission IDs — persisted across app restarts */
    val completedMissionIds = mutableStateListOf<Int>().also { list ->
        prefs.getStringSet("completed_mission_ids", emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.let { list.addAll(it) }
    }

    fun toggleMission(id: Int) {
        if (completedMissionIds.contains(id)) completedMissionIds.remove(id)
        else completedMissionIds.add(id)
        persist()
    }

    fun markComplete(id: Int) {
        if (!completedMissionIds.contains(id)) {
            completedMissionIds.add(id)
            persist()
        }
    }

    private fun persist() {
        prefs.edit()
            .putStringSet("completed_mission_ids", completedMissionIds.map { it.toString() }.toSet())
            .apply()
        // Keep TutorialState in sync so overlay checks still work
        val synced = completedMissionIds.toSet()
        TutorialState.completedMissionIds.removeAll { it !in synced }
        synced.forEach { id -> if (!TutorialState.completedMissionIds.contains(id)) TutorialState.completedMissionIds.add(id) }
    }
}
