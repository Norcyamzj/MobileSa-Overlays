package com.example.mobilesaoverlay

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "MobileSa Overlay"
            textSize = 24f
        }

        val info = TextView(this).apply {
            text = "Painel flutuante de configurações visuais."
            textSize = 16f
        }

        val button = Button(this).apply {
            text = "Iniciar painel"

            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(
                        Intent(this@MainActivity, OverlayService::class.java)
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Painel iniciado!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )

                    startActivity(intent)
                }
            }
        }

        layout.addView(title)
        layout.addView(info)
        layout.addView(button)

        setContentView(layout)
    }
}
