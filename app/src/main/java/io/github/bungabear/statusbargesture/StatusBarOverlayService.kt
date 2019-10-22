package io.github.bungabear.statusbargesture

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import androidx.core.app.NotificationCompat
import java.lang.ref.WeakReference


class StatusBarOverlayService : Service() {
    companion object{
        var isRunning = false
        var windowView : WeakReference<ViewGroup>? = null
    }

    lateinit var windowManager : WindowManager
    var screenWidth = 1

    override fun onBind(intent: Intent?): IBinder?  = null
    override fun onCreate() {
        super.onCreate()
        log()
        isRunning = true
        startForegroundService()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        removeOverlay()

        val view = LayoutInflater.from(this).inflate(R.layout.test, null, false) as ViewGroup
        windowView = WeakReference(view)

        val windowViewLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, 100,
            0, 0, // X, Y 좌표
            if(Build.VERSION.SDK_INT >= 26)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        windowViewLayoutParams.gravity = Gravity.TOP or Gravity.START
        windowManager.addView(view, windowViewLayoutParams)

        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        screenWidth = point.x
//        view.setOnClickListener {
//            log("click")
//        }


        view.setOnTouchListener(object : View.OnTouchListener{
            private var startX = -1
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when(event.action){
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> startX = changeBrightness(startX, event.x)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> startX = -1
                }
                return true
            }
        })
    }

    private fun changeBrightness(start: Int, end: Float) : Int{
        if(start == -1) return end.toInt() // ACTION_DOWN or first ACTION_MOVE
        val percent = (end/screenWidth * 100).toInt()
        log("$start $end $percent $screenWidth")
        Settings.System.putInt(this.contentResolver, Settings.System.SCREEN_BRIGHTNESS, percent)
        return start
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

//        val remoteViews = RemoteViews(packageName, R.layout.notification_service)

        val builder: NotificationCompat.Builder
        val CHANNEL_ID = "StatusBarOverlayService"
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "StatusBar Overlay Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)

        }
        builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.mipmap.ic_launcher)
//            .setContent(remoteViews)
            .setContentIntent(pendingIntent)

        startForeground(1, builder.build())

    }

    private fun removeOverlay(){
        windowView?.get()?.let {
            windowManager.removeViewImmediate(it)
            windowView = null
        }
    }

    override fun onDestroy() {
        log()
        isRunning = false
        removeOverlay()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        log()
//        isRunning = false
        super.onTaskRemoved(rootIntent)
    }

    override fun onLowMemory() {
        log()
        isRunning = false
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        log()
        isRunning = false
        super.onTrimMemory(level)
    }
}