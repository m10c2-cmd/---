package com.yousef.symbolkeyboard

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout

class KeyboardView(
    context: Context,
    private val onChar: (String) -> Unit,
    private val onBackspace: () -> Unit,
    private val onEnter: () -> Unit,
    private val onSymbolTap: (String) -> Unit,
    private val onSpace: () -> Unit
) : LinearLayout(context) {

    private val symbolCycle = listOf("?", "¿", "~", "+")
    private var symbolIndex = 0
    private var currentMode = "letters"

    private val lettersRows = listOf(
        listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج"),
        listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك"),
        listOf("ظ", "ط", "ذ", "د", "ز", "ر", "و", "ة", "ى")
    )

    private val numbersRows = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("@", "#", "$", "_", "&", "-", "(", ")", "/"),
        listOf("*", "\"", "'", ":", ";", "!", "،", "؛", "%")
    )

    private lateinit var keysContainer: LinearLayout
    private lateinit var symbolKeyButton: Button

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#E6E6E6"))
        setPadding(4, 8, 4, 8)
        buildKeyboard()
    }

    private fun buildKeyboard() {
        removeAllViews()
        keysContainer = LinearLayout(context)
        keysContainer.orientation = VERTICAL
        addView(keysContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        val rows = if (currentMode == "letters") lettersRows else numbersRows
        for (row in rows) {
            val rowLayout = LinearLayout(context)
            rowLayout.orientation = HORIZONTAL
            rowLayout.gravity = Gravity.CENTER
            for (key in row) {
                rowLayout.addView(makeKey(key) { onChar(key) })
            }
            keysContainer.addView(rowLayout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        }

        keysContainer.addView(buildBottomRow(), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    private fun buildBottomRow(): LinearLayout {
        val bottom = LinearLayout(context)
        bottom.orientation = HORIZONTAL
        bottom.gravity = Gravity.CENTER

        val modeKey = makeKey(if (currentMode == "letters") "١٢٣" else "أبج", 1.2f) {
            currentMode = if (currentMode == "letters") "numbers" else "letters"
            buildKeyboard()
        }
        bottom.addView(modeKey)

        bottom.addView(makeKey("،", 1f) { onChar("،") })

        symbolKeyButton = Button(context)
        symbolKeyButton.text = symbolCycle[symbolIndex]
        symbolKeyButton.textSize = 20f
        symbolKeyButton.setBackgroundColor(Color.parseColor("#FFFFFF"))

        var isLongPress = false
        val handler = Handler(Looper.getMainLooper())
        val longPressRunnable = Runnable {
            isLongPress = true
            onSpace()
        }
        symbolKeyButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isLongPress = false
                    handler.postDelayed(longPressRunnable, 350)
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(longPressRunnable)
                    if (!isLongPress) {
                        val symbol = symbolCycle[symbolIndex]
                        onSymbolTap(symbol)
                        symbolIndex = (symbolIndex + 1) % symbolCycle.size
                        symbolKeyButton.text = symbolCycle[symbolIndex]
                    }
                    v.performClick()
                }
                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(longPressRunnable)
                }
            }
            true
        }
        bottom.addView(symbolKeyButton, LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f))

        bottom.addView(makeKey(".", 1f) { onChar(".") })
        bottom.addView(makeKey("⏎", 1.2f) { onEnter() })
        bottom.addView(makeKey("⌫", 1.2f) { onBackspace() })

        return bottom
    }

    private fun makeKey(label: String, weight: Float = 1f, action: () -> Unit): Button {
        val b = Button(context)
        b.text = label
        b.textSize = 18f
        b.setPadding(2, 12, 2, 12)
        b.setBackgroundColor(Color.parseColor("#FFFFFF"))
        b.setOnClickListener { action() }
        b.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, weight)
        return b
    }
}
