package com.example.alhecs.game2;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends AppCompatActivity {

    private GameView joguinho;

    private Timer timer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        joguinho = new GameView(this);

        setContentView(joguinho);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Statics.gRunning == joguinho.gameStats)joguinho.pause();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        joguinho.invalidate();
                    }
                });
            }
        }, 0, 1000/Statics.fps);
    }
}
