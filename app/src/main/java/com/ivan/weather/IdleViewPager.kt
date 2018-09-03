package com.ivan.weather

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

//вьюпейджер который не реагирует на жесты чтобы не мешать другим элементом интерфейса
class IdleViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = false
}
