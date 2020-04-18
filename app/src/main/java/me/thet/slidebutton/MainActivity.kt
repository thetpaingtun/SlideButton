package me.thet.slidebutton

import android.content.ClipData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*     root.setOnTouchListener { v, event ->
                 Log.d("Test", "=> ${event.x}")
                 return@setOnTouchListener true
             }*/


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

}
