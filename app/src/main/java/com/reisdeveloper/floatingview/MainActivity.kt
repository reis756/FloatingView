package com.reisdeveloper.floatingview

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var simpleFloatingWindow: SimpleFloatingWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //simpleFloatingWindow = SimpleFloatingWindow(applicationContext)

        button.setOnClickListener {
            if (canDrawOverlays) {
                startFloatingService()
                finish()
            } else {
                startManageDrawOverlaysPermission()
            }
        }
    }

    private fun startManageDrawOverlaysPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${applicationContext.packageName}")
            ).let {
                startActivityForResult(it, REQUEST_CODE_DRAW_OVERLAY_PERMISSION)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_DRAW_OVERLAY_PERMISSION -> {
                if (canDrawOverlays) {
                    startFloatingService()
                    finish()
                } else {
                    showToast("Permission is not granted!")
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5
    }
}