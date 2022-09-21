package com.spotgym.spot.ui.service

import android.content.Context
import android.widget.Toast
import javax.inject.Singleton

interface ToastService {
    fun showText(context: Context, text: CharSequence, duration: Int)
}

@Singleton
class ToastServiceImpl : ToastService {
    override fun showText(
        context: Context,
        text: CharSequence,
        duration: Int,
    ) {
        Toast.makeText(context, text, duration).show()
    }
}
