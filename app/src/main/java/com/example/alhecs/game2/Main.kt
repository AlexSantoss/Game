package com.example.alhecs.game2

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import java.util.Timer
import kotlin.concurrent.schedule


class Main: AppCompatActivity() {
    private lateinit var joguinho: GameView

    private lateinit var timer: Timer
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        joguinho = GameView(this)
        setContentView(joguinho)
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        timer.schedule(0, 1000L/fps){
            handler.post {
                joguinho.invalidate()
            }
        }
    }

    override fun onPause(){
        super.onPause()
        if(gRunning == joguinho.gameStats) joguinho.pause()
        timer.cancel()
    }
}