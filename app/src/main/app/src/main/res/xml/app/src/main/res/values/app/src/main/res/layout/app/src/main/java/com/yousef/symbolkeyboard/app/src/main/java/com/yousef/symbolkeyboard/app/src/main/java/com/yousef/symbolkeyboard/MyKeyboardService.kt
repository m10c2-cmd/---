package com.yousef.symbolkeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo

class MyKeyboardService : InputMethodService() {

    private val wordBuilder = StringBuilder()

    override fun onCreateInputView(): View {
        return KeyboardView(
            this,
            onChar = { char -> handleChar(char) },
            onBackspace = { handleBackspace() },
            onEnter = { handleEnter() },
            onSymbolTap = { symbol -> handleBoundary(symbol) },
            onSpace = { handleBoundary(" ") }
        )
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        wordBuilder.clear()
    }

    private fun handleChar(char: String) {
        val ic = currentInputConnection ?: return
        if (char == "،" || char == ".") {
            handleBoundary(char)
            return
        }
        ic.commitText(char, 1)
        wordBuilder.append(char)
    }

    private fun handleBoundary(separator: String) {
        val ic = currentInputConnection ?: return
        val word = wordBuilder.toString()
        if (word.isNotEmpty()) {
            val expansion = ShortcutManager.expand(this, word)
            if (expansion != null) {
                ic.deleteSurroundingText(word.length, 0)
                ic.commitText(expansion, 1)
            }
        }
        ic.commitText(separator, 1)
        wordBuilder.clear()
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return
        ic.deleteSurroundingText(1, 0)
        if (wordBuilder.isNotEmpty()) {
            wordBuilder.deleteCharAt(wordBuilder.length - 1)
        }
    }

    private fun handleEnter() {
        val ic = currentInputConnection ?: return
        wordBuilder.clear()
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
    }
}
