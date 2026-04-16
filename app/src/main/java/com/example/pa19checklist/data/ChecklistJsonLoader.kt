package com.example.pa19checklist.data

import android.content.Context
import com.example.pa19checklist.data.model.Aircraft
import com.example.pa19checklist.data.model.Item
import com.example.pa19checklist.data.model.Phase
import org.json.JSONObject

class ChecklistJsonLoader(private val context: Context) {
    fun loadAircraft(assetName: String = "checklist_pa19.json"): Aircraft {
        val json = context.assets.open(assetName).bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val phasesJson = root.getJSONArray("phases")

        return Aircraft(
            name = root.optString("aircraft", root.optString("name", "PA19")),
            phases = buildList {
                for (phaseIndex in 0 until phasesJson.length()) {
                    val phaseJson = phasesJson.getJSONObject(phaseIndex)
                    val itemsJson = phaseJson.getJSONArray("items")

                    add(
                        Phase(
                            name = phaseJson.getString("name"),
                            items = buildList {
                                for (itemIndex in 0 until itemsJson.length()) {
                                    val itemJson = itemsJson.getJSONObject(itemIndex)
                                    add(Item(label = itemJson.getString("label")))
                                }
                            }
                        )
                    )
                }
            }
        )
    }
}
