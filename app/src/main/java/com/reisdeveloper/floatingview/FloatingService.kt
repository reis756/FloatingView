package com.reisdeveloper.floatingview

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class FloatingService : Service(){
    override fun onBind(intent: Intent?): IBinder? = null

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

    private fun showNotification() {

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val exitIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_EXIT)
        }

        val noteIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_NOTE)
        }

        val exitPendingIntent = PendingIntent.getService(
            this, CODE_EXIT_INTENT, exitIntent, 0
        )

        val notePendingIntent = PendingIntent.getService(
            this, CODE_NOTE_INTENT, noteIntent, 0
        )

        // From Android O, it's necessary to create a notification channel first.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                with(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_GENERAL,
                        getString(R.string.notification_channel_general),
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                ) {
                    enableLights(false)
                    setShowBadge(false)
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    manager.createNotificationChannel(this)
                }
            } catch (ignored: Exception) {
                // Ignore exception.
            }
        }

        with(
            NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_GENERAL
            )
        ) {
            setTicker(null)
            setContentTitle(getString(R.string.app_name))
            setContentText(getString(R.string.notification_text))
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            priority = Notification.PRIORITY_DEFAULT
            setContentIntent(notePendingIntent)
            addAction(
                NotificationCompat.Action(
                    0,
                    getString(R.string.notification_exit),
                    exitPendingIntent
                )
            )
            startForeground((CODE_FOREGROUND_SERVICE + System.currentTimeMillis() % 10000).toInt(), build())
        }

    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val command = intent.getStringExtra(INTENT_COMMAND)
        if (command == INTENT_COMMAND_EXIT) {
            stopService()
            return START_NOT_STICKY
        }

        showNotification()

        val window = SimpleFloatingWindow(this)
        window.show()

        return START_STICKY
    }

    companion object{
        const val INTENT_COMMAND = "com.localazy.quicknote.COMMAND"
        const val INTENT_COMMAND_EXIT = "EXIT"
        const val INTENT_COMMAND_NOTE = "NOTE"

        private const val NOTIFICATION_CHANNEL_GENERAL = "quicknote_general"
        private const val CODE_FOREGROUND_SERVICE = 1
        private const val CODE_EXIT_INTENT = 2
        private const val CODE_NOTE_INTENT = 3
    }
}