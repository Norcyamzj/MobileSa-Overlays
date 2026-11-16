package com.example.mobilesaoverlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Switch

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (overlayView == null) {
            showOverlay()
        }

        return START_NOT_STICKY
    }

    private fun showOverlay() {

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 20, 24, 20)
            setBackgroundColor(0xEE202020.toInt())
        }

        val title = TextView(this).apply {
            text = "MobileSa Overlay"
            textSize = 18f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 0, 0, 16)
        }

        val closeButton = Button(this).apply {
            text = "Fechar painel"
            setOnClickListener {
                stopSelf()
            }
        }

        val visualSwitch = Switch(this).apply {
            text = "Opção visual"
            setTextColor(0xFFFFFFFF.toInt())
        }

        val teamSwitch = Switch(this).apply {
            text = "Filtro visual"
            setTextColor(0xFFFFFFFF.toInt())
        }

        val fovLabel = TextView(this).apply {
            text = "FOV visual"
            setTextColor(0xFFFFFFFF.toInt())
        }

        val fovBar = SeekBar(this).apply {
            max = 100
            progress = 50
        }

        val smoothLabel = TextView(this).apply {
            text = "Suavização visual"
            setTextColor(0xFFFFFFFF.toInt())
        }

        val smoothBar = SeekBar(this).apply {
            max = 100
            progress = 50
        }

        panel.addView(title)
        panel.addView(visualSwitch)
        panel.addView(teamSwitch)
        panel.addView(fovLabel)
        panel.addView(fovBar)
        panel.addView(smoothLabel)
        panel.addView(smoothBar)
        panel.addView(closeButton)

        val overlayType =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 120
        }

        title.setOnTouchListener(object : View.OnTouchListener {

            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(
                view: View,
                event: MotionEvent
            ): Boolean {

                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX +
                            (event.rawX - initialTouchX).toInt()

                        params.y = initialY +
                            (event.rawY - initialTouchY).toInt()

                        windowManager.updateViewLayout(panel, params)
                        return true
                    }
                }

                return false
            }
        })

        overlayView = panel
        windowManager.addView(panel, params)
    }

    override fun onDestroy() {
        super.onDestroy()

        overlayView?.let {
            windowManager.removeView(it)
        }

        overlayView = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
