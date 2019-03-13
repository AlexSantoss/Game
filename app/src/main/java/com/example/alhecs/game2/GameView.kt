package com.example.alhecs.game2

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class GameView(ctx: Context): View(ctx) {

    var gameStats = gRunning

    var telaH = 0
    var telaW = 0

    var nave = createAndResizeBitmap(R.drawable.nave, naveW, naveH)
    var naveX = 0F
    var naveY = 0F
    var score = 0
    var life = numLifes

    var statsBitmap = Array<Bitmap>(2){idx ->
        if(idx == 0) createAndResizeBitmap(R.drawable.start, buttonPSW, buttonPSH)
        else createAndResizeBitmap(R.drawable.pause, buttonPSW, buttonPSH)
    }
    var bpsX = 0F
    var bpsY = 0F

    var scorePaint = Paint().apply {
        color = Color.BLACK
        textSize = 32f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    var executor = Executors.newScheduledThreadPool(2)

    var tiro = createAndResizeBitmap(R.drawable.tiro, tiroW, tiroH)
    var bps = 5
    var bs = 0F
    var tiros = ArrayList<Tiro>()
    var createTiro = Runnable {
        val novoTiro = Tiro(naveX + (naveW - tiroW) / 2.0f, naveY + tiroH / 2.0f)
        tiros.add(novoTiro)
        val removidos = ArrayList<Tiro>()
        for (tiro in tiros) if (tiro.posY + tiroH < 0) removidos.add(tiro)
        for (tiro in removidos) tiros.remove(tiro)
    }
    var futureCreateTiro = executor.scheduleAtFixedRate(createTiro, 0, (1000 / bps).toLong(), TimeUnit.MILLISECONDS)

    var inimigo = createAndResizeBitmap(R.drawable.nave_ini, naveInimigaW, naveInimigaH)
    var inimigos = ArrayList<NaveInimiga>()
    val createInimigo = Runnable {
        val novo = NaveInimiga(telaW, telaH)
        inimigos.add(novo)
        val removidos = ArrayList<NaveInimiga>()
        for (inimigo in inimigos) if (inimigo.posY > telaH) removidos.add(inimigo)
        for (inimigo in removidos) inimigos.remove(inimigo)
    }
    var futureCreateInimigo = executor.scheduleAtFixedRate(createInimigo, 0, 1000, TimeUnit.MILLISECONDS)

    var pausedTime = 0L
    var ltpX = 0
    var ltpY = 0

    init{
        Log.d("debug", "altura " + Integer.toString(height))
        Log.d("debug", "largura " + Integer.toString(width))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (naveY == 0f) {
            telaH = height
            telaW = width
            naveY = telaH - naveH * 1.5f
            naveX = (telaW - naveW) / 2.0f
            bpsX = telaW.toFloat() - buttonPSW.toFloat() - buttonPSRM
            bpsY = buttonPSTM.toFloat()
            bs = telaH * 0.75f
        }

        if (gameStats == gRunning) {
            canvas?.drawBitmap(statsBitmap[gPaused - 1], bpsX, bpsY, null)
            moveTiro()
            moveInimigos()
            pausedTime = 0
        } else if (gameStats == gPaused) {
            canvas?.drawBitmap(statsBitmap[gRunning - 1], bpsX, bpsY, null)
        }

        canvas?.drawText("Score : " + Integer.toString(score), 0f, 48f, scorePaint)
        drawTiros(canvas!!)
        drawInimigos(canvas)
        canvas.drawBitmap(nave, naveX, naveY, null)
    }

    private fun moveInimigos() {
        val copia = ArrayList(inimigos)
        val now = System.currentTimeMillis()
        for (nave in copia) {
            if (nave != null) {
                //float dif = (float) (now - nave.getTimeC()) / 1000;
                //nave.setPosY(nave.getPosY() - bs * dif);
                nave.posY = nave.posY + nave.acelY
                nave.timeC = now
            }
        }
    }

    private fun drawInimigos(canvas: Canvas) {
        val copia = ArrayList(inimigos)
        for (inimigo in copia) {
            if (inimigo != null) {
                if (inimigo.life > 0)
                    canvas.drawBitmap(this.inimigo, inimigo.posX, inimigo.posY, null)
            }
        }
    }

    private fun drawTiros(canvas: Canvas) {
        val copia = ArrayList(tiros)
        for (tiro in copia) {
            if (tiro != null) {
                if (tiro.visivel == 1)
                    canvas.drawBitmap(this.tiro, tiro.posX, tiro.posY, null)
            }
        }
    }

    private fun moveTiro() {
        val copia = ArrayList(tiros)
        val now = System.currentTimeMillis()
        for (tiro in copia) {
            if (tiro != null) {
                val dif = (now - tiro.timeC).toFloat() / 1000
                tiro.posY = tiro.posY - bs * dif
                tiro.timeC = now

                colision(ArrayList(inimigos), tiro)
            }
        }
    }

    private fun colision(naves: List<NaveInimiga>, tiro: Tiro) {
        for (nave in naves) {
            if (nave.life > 0 && tiro.visivel == 1 &&
                    tiro.posX + tiroW / 2 > nave.posX &&
                    tiro.posX - tiroW / 2 < nave.posX + naveInimigaW &&
                    tiro.posY + tiroH / 2 > nave.posY &&
                    tiro.posY - tiroH / 2 < nave.posY + naveInimigaH) {

                nave.life = nave.life - 1
                tiro.visivel = 0
                if (nave.life == 0) score += nave.score
            }
        }
    }

    fun pause() {
        gameStats = gPaused
        pausedTime = System.currentTimeMillis()
        futureCreateTiro.cancel(false)
        futureCreateInimigo.cancel(false)
    }

    fun resume() {
        gameStats = gRunning
        pausedTime = System.currentTimeMillis() - pausedTime

        for (tiro in tiros) tiro.timeC = tiro.timeC + pausedTime
        for (inimigo in inimigos) inimigo.timeC = inimigo.timeC + pausedTime

        futureCreateTiro = executor.scheduleAtFixedRate(createTiro, 0, (1000 / bps).toLong(), TimeUnit.MILLISECONDS)
        futureCreateInimigo = executor.scheduleAtFixedRate(createInimigo, 0, 1000, TimeUnit.MILLISECONDS)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val posX = e.x.toInt()
        val posY = e.y.toInt()
        when (e.action) {
            MotionEvent.ACTION_MOVE -> if (gameStats == gRunning && ltpX != 0) {
                if (ltpX > naveX && ltpX < naveX + naveW &&
                        ltpY > naveY && ltpY < naveY + naveH) {
                    val diferencaX = posX - ltpX
                    if (diferencaX < 150 && diferencaX > -150 &&
                            naveX + diferencaX > 0 && naveX + diferencaX < telaW - naveW)
                        naveX += diferencaX
                    ltpX = posX

                    val diferencaY = posY - ltpY
                    if (diferencaY < 150 && diferencaY > -150 &&
                            naveY + diferencaY > buttonPSTM * 2 + buttonPSH &&
                            naveY + diferencaY < telaH - naveH)
                        naveY += diferencaY
                    ltpY = posY
                }
            }
            MotionEvent.ACTION_DOWN -> if (posX > bpsX && posX < bpsX + buttonPSW
                    && posY > bpsY && posY < bpsY + buttonPSH) {
                gameStats = if (gameStats == gRunning) gPaused else gRunning
                if (gameStats == gPaused)
                    pause()
                else
                    resume()
            } else {
                ltpX = posX
                ltpY = posY
            }
            MotionEvent.ACTION_UP -> {
                ltpX = 0
                ltpY = 0
            }
        }
        return true
    }

}