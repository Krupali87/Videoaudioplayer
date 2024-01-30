package com.app.videoplayer.Class

import android.view.View
import javax.security.auth.callback.Callback

class DoubleClickListener(private val doubleClickTimeLimitMills: Long = 500, private val callback: Callback) : View.OnClickListener{
    private var lastClicked: Long = -1L

    override fun onClick(p0: View?) {
        lastClicked = when {
            lastClicked == -1L -> {
                System.currentTimeMillis()
            }
            isDoubleClicked() -> {
                callback.doubleClicked()
                -1L
            }
            else -> {
                System.currentTimeMillis()
            }
        }

    }
    private fun getTimeDiff(from: Long, to: Long): Long {
        return to - from
    }
    private fun isDoubleClicked(): Boolean {
        return getTimeDiff(
            lastClicked,
            System.currentTimeMillis()
        ) <= doubleClickTimeLimitMills
    }
    interface Callback {
        fun doubleClicked()
    }

}