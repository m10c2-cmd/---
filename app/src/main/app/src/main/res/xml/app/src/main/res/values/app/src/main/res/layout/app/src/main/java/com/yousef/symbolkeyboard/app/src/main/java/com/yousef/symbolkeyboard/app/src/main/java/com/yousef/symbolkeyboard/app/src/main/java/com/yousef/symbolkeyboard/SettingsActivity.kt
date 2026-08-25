package com.yousef.symbolkeyboard

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val editShortcut = findViewById<EditText>(R.id.editShortcut)
        val editExpansion = findViewById<EditText>(R.id.editExpansion)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        container = findViewById(R.id.shortcutsContainer)

        btnAdd.setOnClickListener {
            val s = editShortcut.text.toString().trim()
            val e = editExpansion.text.toString().trim()
            if (s.isNotEmpty() && e.isNotEmpty()) {
                ShortcutManager.addShortcut(this, s, e)
                editShortcut.text.clear()
                editExpansion.text.clear()
                refreshList()
            }
        }

        refreshList()
    }

    private fun refreshList() {
        container.removeAllViews()
        val map = ShortcutManager.getAll(this)
        for ((k, v) in map) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL

            val text = TextView(this)
            text.text = "$k  ⇐  $v"
            text.textSize = 15f
            text.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val del = Button(this)
            del.text = "حذف"
            del.setOnClickListener {
                ShortcutManager.removeShortcut(this, k)
                refreshList()
            }

            row.addView(text)
            row.addView(del)
            container.addView(row)
        }
    }
}
