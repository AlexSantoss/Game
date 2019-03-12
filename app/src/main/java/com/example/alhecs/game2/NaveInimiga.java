package com.example.alhecs.game2;

public class NaveInimiga {
    private int life;

    private float posX;
    private float posY;

    private int score;

    private float acelX;
    private float acelY;

    private long timeC;

    public int getScore() {
        return score;
    }

    public long getTimeC() {
        return timeC;
    }

    public void setTimeC(long timeC) {
        this.timeC = timeC;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getAcelX() {
        return acelX;
    }

    public void setAcelX(float acelX) {
        this.acelX = acelX;
    }

    public float getAcelY() {
        return acelY;
    }

    public void setAcelY(float acelY) {
        this.acelY = acelY;
    }



    public NaveInimiga(int larguraTela, int alturaTela){
        life = (int) Math.ceil(Math.random() * 2) + 1;

        posX = (float) (Math.random() * (larguraTela-Statics.naveInimigaW))%larguraTela;
        posY = -Statics.naveInimigaH;

        acelX = 0;
        acelY = (int) Math.ceil(Math.random() * 10) + 5;

        timeC = System.currentTimeMillis();
        score = life * 10;
    }
}
