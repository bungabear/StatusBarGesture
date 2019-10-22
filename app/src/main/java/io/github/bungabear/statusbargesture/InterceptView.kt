package io.github.bungabear.statusbargesture

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

class InterceptView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        Log.d("intercept", ev.toString())
//        return false
//    }
}