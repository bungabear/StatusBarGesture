package io.github.bungabear.statusbargesture

import android.app.Activity
import android.app.Service
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.Window


object Util {
    fun getStatuabarHeight(window : Window): Int {
        val rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight = rectangle.top
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        return contentViewTop - statusBarHeight
    }
}

fun Activity.log(message: String = ""){
    if (BuildConfig.DEBUG) {
        val caller = Thread.currentThread().stackTrace[4].methodName
        Log.d(this::class.java.simpleName, "$caller > $message")
    }
}

fun Service.log(message: String = ""){
    if (BuildConfig.DEBUG) {
        val caller = Thread.currentThread().stackTrace[4].methodName
        Log.d(this::class.java.simpleName, "$caller > $message")
    }
}
