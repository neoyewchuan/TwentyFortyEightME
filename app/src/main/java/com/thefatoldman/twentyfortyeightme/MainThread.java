package com.thefatoldman.twentyfortyeightme;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private GameManager gameManager;
    private int targetFPS = 60;
    private Canvas canvas;
    private boolean running;

    public MainThread(SurfaceHolder surfaceHolder, GameManager gamemanager) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameManager = gamemanager;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder)   {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {
        long startTime, timeMillis, waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / targetFPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)    {
                    gameManager.update();
                    gameManager.draw(canvas);
                }
            } catch (Exception e)   {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e)   {
                        e.printStackTrace();
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                if (waitTime > 0)   {
                    sleep(waitTime);
                }
            } catch (Exception e)   {
                e.printStackTrace();
            }

        }
    }
}
