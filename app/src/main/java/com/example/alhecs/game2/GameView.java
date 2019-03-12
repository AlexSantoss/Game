package com.example.alhecs.game2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameView extends View{

    public int gameStats;

    private int telaW;
    private int telaH;

    private Bitmap nave;
    private float naveX;
    private float naveY;
    private int score;
    private int life;

    private Bitmap statsBitmap[];
    private float bpsX;
    private float bpsY;

    private Paint scorePaint;

    private Bitmap tiro;
    private List<Tiro> tiros;
    private int bps = 5;
    private float bs;

    private Bitmap inimigo;
    private List<NaveInimiga> inimigos;

    private ScheduledExecutorService executor;

    private ScheduledFuture<?> futureCreateTiro;
    private Runnable createTiro = new Runnable() {
        @Override
        public void run() {
            Tiro novoTiro = new Tiro(naveX+(Statics.naveW-Statics.tiroW)/2.0f,naveY+Statics.tiroH/2.0f);
            tiros.add(novoTiro);
            List<Tiro> removidos = new ArrayList<>();
            for(Tiro tiro : tiros) if(tiro.getPosY() + Statics.tiroH < 0) removidos.add(tiro);
            for(Tiro tiro : removidos) tiros.remove(tiro);
        }
    };

    private ScheduledFuture<?> futureCreateInimigo;
    private Runnable createInimigo = new Runnable() {
        @Override
        public void run() {
            NaveInimiga novo= new NaveInimiga(telaW, telaH);
            inimigos.add(novo);
            List<NaveInimiga> removidos = new ArrayList<>();
            for(NaveInimiga inimigo : inimigos) if(inimigo.getPosY() > telaH) removidos.add(inimigo);
            for(NaveInimiga inimigo : removidos) inimigos.remove(inimigo);
        }
    };

    //ltp = last touch position
    private int ltpX;
    private long pausedTime;
    private int ltpY;

    public GameView(Context context) {
        super(context);
        life = Statics.numLifes;

        nave = BitmapFactory.decodeResource(getResources(), R.drawable.nave);
        nave = Bitmap.createScaledBitmap(nave, Statics.naveW, Statics.naveH, false);

        tiro = BitmapFactory.decodeResource(getResources(), R.drawable.tiro);
        tiro = Bitmap.createScaledBitmap(tiro, Statics.tiroW, Statics.tiroH, false);

        inimigo = BitmapFactory.decodeResource(getResources(), R.drawable.nave_ini);
        inimigo = Bitmap.createScaledBitmap(inimigo, Statics.naveInimigaW, Statics.naveInimigaH, false);

        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(32);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setAntiAlias(true);

        statsBitmap = new Bitmap[2];

        statsBitmap[Statics.gPaused-1] = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        statsBitmap[Statics.gPaused-1] = Bitmap.createScaledBitmap(statsBitmap[Statics.gPaused-1],
                Statics.buttonPSW, Statics.buttonPSH, false);

        statsBitmap[Statics.gRunning-1] = BitmapFactory.decodeResource(getResources(), R.drawable.start);
        statsBitmap[Statics.gRunning-1] = Bitmap.createScaledBitmap(statsBitmap[Statics.gRunning-1],
                Statics.buttonPSW, Statics.buttonPSH, false);

        executor = Executors.newScheduledThreadPool(2);

        tiros = new ArrayList<>();
        futureCreateTiro = executor.scheduleAtFixedRate(createTiro,0,1000/bps, TimeUnit.MILLISECONDS);

        inimigos = new ArrayList<>();
        futureCreateInimigo = executor.scheduleAtFixedRate(createInimigo,0,1000, TimeUnit.MILLISECONDS);



        gameStats = Statics.gRunning;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(naveY == 0) {
            telaH = getHeight();
            telaW = getWidth();
            naveY = telaH - Statics.naveH * 1.5f;
            naveX = (telaW - Statics.naveW)/2.0f;
            bpsX = telaW - Statics.buttonPSW - Statics.buttonPSRM;
            bpsY = Statics.buttonPSTM;
            bs = telaH*0.75f;
            Log.d("debug", "altura " +Integer.toString(telaH));
            Log.d("debug", "largura " +Integer.toString(telaW));
        }

        if(gameStats == Statics.gRunning) {
            canvas.drawBitmap(statsBitmap[Statics.gPaused - 1], bpsX, bpsY, null);
            moveTiro();
            moveInimigos();
            pausedTime = 0;
        }else if(gameStats == Statics.gPaused){
            canvas.drawBitmap(statsBitmap[Statics.gRunning - 1], bpsX, bpsY, null);
        }

        canvas.drawText("Score : " + Integer.toString(score), 0, 48, scorePaint);
        drawTiros(canvas);
        drawInimigos(canvas);
        canvas.drawBitmap(nave, naveX, naveY, null);
    }

    private void moveInimigos() {
        List<NaveInimiga> copia = new ArrayList<>(inimigos);
        long now = System.currentTimeMillis();
        for(NaveInimiga nave : copia) {
            if(nave != null) {
                //float dif = (float) (now - nave.getTimeC()) / 1000;
                //nave.setPosY(nave.getPosY() - bs * dif);
                nave.setPosY(nave.getPosY() + nave.getAcelY());
                nave.setTimeC(now);
            }
        }
    }

    private void drawInimigos(Canvas canvas) {
        List<NaveInimiga> copia = new ArrayList<>(inimigos);
        for(NaveInimiga inimigo : copia) {
            if (inimigo != null) {
                if (inimigo.getLife() > 0)
                    canvas.drawBitmap(this.inimigo, inimigo.getPosX(), inimigo.getPosY(), null);
            }
        }
    }

    private void drawTiros(Canvas canvas) {
        List<Tiro> copia = new ArrayList<>(tiros);
        for(Tiro tiro : copia) {
            if (tiro != null){
                if (tiro.getVisivel() == 1)
                    canvas.drawBitmap(this.tiro, tiro.getPosX(), tiro.getPosY(), null);
            }
        }
    }

    private void moveTiro(){
        List<Tiro> copia = new ArrayList<>(tiros);
        long now = System.currentTimeMillis();
        for(Tiro tiro : copia) {
            if(tiro != null) {
                float dif = (float) (now - tiro.getTimeC()) / 1000;
                tiro.setPosY(tiro.getPosY() - bs * dif);
                tiro.setTimeC(now);

                colision(new ArrayList<>(inimigos), tiro);
            }
        }
    }

    private void colision(List<NaveInimiga> naves, Tiro tiro) {
        for(NaveInimiga nave: naves){
            if(nave != null && nave.getLife() > 0 && tiro.getVisivel() == 1 &&
                    tiro.getPosX()+Statics.tiroW/2 > nave.getPosX() &&
                    tiro.getPosX()-Statics.tiroW/2 < nave.getPosX()+Statics.naveInimigaW &&
                    tiro.getPosY()+Statics.tiroH/2 > nave.getPosY() &&
                    tiro.getPosY()-Statics.tiroH/2 < nave.getPosY()+Statics.naveInimigaH) {

                nave.setLife(nave.getLife() - 1);
                tiro.setVisivel(0);
                if(nave.getLife() == 0) score += nave.getScore();
            }
        }
    }

    public void pause(){
        gameStats = Statics.gPaused;
        pausedTime = System.currentTimeMillis();
        futureCreateTiro.cancel(false);
        futureCreateInimigo.cancel(false);
    }

    public void resume() {
        gameStats = Statics.gRunning;
        pausedTime = System.currentTimeMillis()-pausedTime;

        for(Tiro tiro:tiros) tiro.setTimeC(tiro.getTimeC() + pausedTime);
        for(NaveInimiga inimigo:inimigos) inimigo.setTimeC(inimigo.getTimeC() + pausedTime);

        futureCreateTiro = executor.scheduleAtFixedRate(createTiro,0,1000/bps, TimeUnit.MILLISECONDS);
        futureCreateInimigo = executor.scheduleAtFixedRate(createInimigo,0,1000, TimeUnit.MILLISECONDS);
    }

    public boolean onTouchEvent(MotionEvent e) {
        int posX = (int) e.getX();
        int posY = (int) e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if(gameStats == Statics.gRunning && ltpX != 0) {
                    if(ltpX > naveX && ltpX < naveX + Statics.naveW &&
                            ltpY > naveY && ltpY < naveY + Statics.naveH) {
                        int diferencaX = posX - ltpX;
                        if (diferencaX < 150 && diferencaX > -150 &&
                                naveX + diferencaX > 0 && naveX + diferencaX < telaW - Statics.naveW)
                            naveX = diferencaX + naveX;
                        ltpX = posX;

                        int diferencaY = posY - ltpY;
                        if (diferencaY < 150 && diferencaY > -150 &&
                                naveY + diferencaY > Statics.buttonPSTM * 2 + Statics.buttonPSH &&
                                naveY + diferencaY < telaH - Statics.naveH)
                            naveY = diferencaY + naveY;
                        ltpY = posY;
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if(posX > bpsX && posX < bpsX + Statics.buttonPSW
                        && posY > bpsY && posY < bpsY + Statics.buttonPSH) {
                    gameStats = (gameStats == Statics.gRunning) ? Statics.gPaused : Statics.gRunning;
                    if(gameStats == Statics.gPaused) pause();
                    else resume();
                }
                else{
                    ltpX = posX;
                    ltpY = posY;
                }
                break;
            case MotionEvent.ACTION_UP:
                ltpX = 0;
                ltpY = 0;
                break;
        }
        return true;
    }

}
