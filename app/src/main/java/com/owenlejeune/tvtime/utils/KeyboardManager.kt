package com.owenlejeune.tvtime.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

class KeyboardManager private constructor(context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: KeyboardManager? = null
        private val lock = Object()

        fun getInstance(context: Context): KeyboardManager {
            if (INSTANCE == null) {
                synchronized(lock) {
                    if (INSTANCE == null) {
                        INSTANCE = KeyboardManager(context)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private val activity = context as Activity
    private var keyboardDismissListeners: MutableList<KeyboardDismissListener> = ArrayList()

    private abstract class KeyboardDismissListener(
        private val rootView: View,
        private val onKeyboardDismiss: () -> Unit
    ) : ViewTreeObserver.OnGlobalLayoutListener {
        private var isKeyboardClosed: Boolean = false
        override fun onGlobalLayout() {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                // 0.15 ratio is right enough to determine keypad height.
                isKeyboardClosed = false
            } else if (!isKeyboardClosed) {
                isKeyboardClosed = true
                onKeyboardDismiss.invoke()
            }
        }
    }

    fun attachKeyboardDismissListener(onKeyboardDismiss: () -> Unit) {
        val rootView = activity.findViewById<View>(android.R.id.content)
        keyboardDismissListeners.add(object : KeyboardDismissListener(rootView, onKeyboardDismiss) {})
        keyboardDismissListeners.forEach {
            rootView.viewTreeObserver.addOnGlobalLayoutListener(it)
        }
    }

    fun release() {
        val rootView = activity.findViewById<View>(android.R.id.content)
        keyboardDismissListeners.forEach {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(it)
        }
        keyboardDismissListeners.clear()
    }

}