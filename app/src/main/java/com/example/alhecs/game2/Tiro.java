package com.example.alhecs.game2;

class Tiro {

    private long timeC;
    private float posX;
    private float posY;
    private int visivel;


    public Tiro(float posX, float posY){
        this.posX = posX;
        this.posY = posY;
        timeC = System.currentTimeMillis();
        visivel = 1;

    }

    public int getVisivel() {
        return visivel;
    }

    public void setVisivel(int visivel) {
        this.visivel = visivel;
    }

    public long getTimeC() {
        return timeC;
    }

    public void setTimeC(long timeC) {
        this.timeC = timeC;
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
}
