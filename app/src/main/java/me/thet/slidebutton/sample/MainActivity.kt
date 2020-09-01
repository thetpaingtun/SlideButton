package me.thet.slidebutton.sample

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


/*
        btn.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val clipData = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDragAndDrop(clipData, shadowBuilder, v, 0)
                v.visibility = View.VISIBLE
                return@setOnTouchListener true
            } else return@setOnTouchListener false
        }*/
    }

    fun reset(view: View) {
        setContentView(R.layout.activity_main)
    }

}
