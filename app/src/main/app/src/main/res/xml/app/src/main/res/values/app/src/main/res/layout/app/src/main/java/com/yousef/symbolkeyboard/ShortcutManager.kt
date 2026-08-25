package com.yousef.symbolkeyboard

import android.content.Context
import org.json.JSONObject

object ShortcutManager {
    private const val PREFS = "shortcuts_prefs"
    private const val KEY_DATA = "shortcuts_json"

    fun getAll(context: Context): MutableMap<String, String> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_DATA, "{}") ?: "{}"
        val json = JSONObject(raw)
        val map = LinkedHashMap<String, String>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            map[k] = json.getString(k)
        }
        return map
    }

    private fun save(context: Context, map: Map<String, String>) {
        val json = JSONObject()
        map.forEach { (k, v) -> json.put(k, v) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_DATA, json.toString()).apply()
    }

    fun addShortcut(context: Context, shortcut: String, expansion: String) {
        val map = getAll(context)
        map[shortcut] = expansion
        save(context, map)
    }

    fun removeShortcut(context: Context, shortcut: String) {
        val map = getAll(context)
        map.remove(shortcut)
        save(context, map)
    }

    fun expand(context: Context, word: String): String? {
        if (word.isBlank()) return null
        return getAll(context)[word]
    }
}
