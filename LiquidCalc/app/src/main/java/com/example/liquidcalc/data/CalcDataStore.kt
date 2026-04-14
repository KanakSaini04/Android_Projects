package com.example.liquidcalc.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calc_prefs")

class CalcDataStore(private val context: Context) {

    companion object {
        private val KEY_HISTORY = stringPreferencesKey("history")
        private val KEY_BG_URI = stringPreferencesKey("background_uri")
        private const val MAX_HISTORY = 30
    }

    // ── History ──────────────────────────────────────────────────────────────

    val historyFlow: Flow<List<HistoryEntry>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_HISTORY] ?: "[]"
        parseHistory(raw)
    }

    suspend fun addHistoryEntry(entry: HistoryEntry) {
        context.dataStore.edit { prefs ->
            val current = parseHistory(prefs[KEY_HISTORY] ?: "[]").toMutableList()
            current.add(0, entry)
            if (current.size > MAX_HISTORY) current.subList(MAX_HISTORY, current.size).clear()
            prefs[KEY_HISTORY] = serializeHistory(current)
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { prefs -> prefs[KEY_HISTORY] = "[]" }
    }

    // ── Background URI ───────────────────────────────────────────────────────

    val backgroundUriFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_BG_URI]
    }

    suspend fun saveBackgroundUri(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri == null) prefs.remove(KEY_BG_URI) else prefs[KEY_BG_URI] = uri
        }
    }

    // ── Serialization ────────────────────────────────────────────────────────

    private fun parseHistory(json: String): List<HistoryEntry> {
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                HistoryEntry(
                    expression = obj.getString("expr"),
                    result = obj.getString("result"),
                    timestamp = obj.getLong("ts")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serializeHistory(list: List<HistoryEntry>): String {
        val arr = JSONArray()
        list.forEach { entry ->
            val obj = JSONObject()
            obj.put("expr", entry.expression)
            obj.put("result", entry.result)
            obj.put("ts", entry.timestamp)
            arr.put(obj)
        }
        return arr.toString()
    }
}

data class HistoryEntry(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)