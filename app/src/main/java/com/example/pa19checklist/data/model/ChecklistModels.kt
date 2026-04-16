package com.example.pa19checklist.data.model

data class Aircraft(
    val name: String,
    val phases: List<Phase>
)

data class Phase(
    val name: String,
    val items: List<Item>
)

data class Item(
    val label: String
)

data class SessionState(
    val currentPhaseIndex: Int = 0,
    val currentItemIndex: Int = 0
)
