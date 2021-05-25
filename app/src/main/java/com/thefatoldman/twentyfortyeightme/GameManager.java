package com.thefatoldman.twentyfortyeightme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.thefatoldman.twentyfortyeightme.sprites.EndGame;
import com.thefatoldman.twentyfortyeightme.sprites.Grid;
import com.thefatoldman.twentyfortyeightme.sprites.Score;

public class GameManager extends SurfaceView implements SurfaceHolder.Callback, SwipeCallback, GameManagerCallback {

    private static final String APP_NAME = "Twenty Forty-Eight";
    private MainThread thread;
    private Grid grid;
    private int scrWidth, scrHeight, stdSize;
    private TileManager tileManager;
    private SwipeListener swipe;
    private EndGame endgameSprite;
    private boolean endGame = false;
    private Score score;
    private Bitmap restartButton;
    private int restartButtonX, restartButtonY, restartButtonSize;
    private InterstitialAd interstitialAd;
    private boolean interstitialShown = false;
    private boolean isAdsFree = false;

    public GameManager(Context context, AttributeSet attrs)  {
        super(context, attrs);
        setLongClickable(true);

        isAdsFree = (context.getString(R.string.ads_free).equals("true"));
        if (!isAdsFree) {
            interstitialAd = new InterstitialAd(getContext());
            interstitialAd.setAdUnitId(context.getString(R.string.interstitial_ads_id));
        }
        getHolder().addCallback(this);
        swipe = new SwipeListener(getContext(), this);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        scrWidth = dm.widthPixels;
        scrHeight = dm.heightPixels;
        stdSize = (int) (scrWidth * .88) / 4;
        grid = new Grid(getResources(), scrWidth, scrHeight, stdSize);
        tileManager = new TileManager(getResources(), stdSize, scrWidth, scrHeight, this);
        endgameSprite = new EndGame(getResources(), scrWidth, scrHeight);
        score = new Score(getResources(), scrWidth, scrHeight, stdSize, getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE));

        restartButtonSize = (int) getResources().getDimension(R.dimen.restart_button_size);
        Bitmap bmpRestart = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
        restartButton = Bitmap.createScaledBitmap(bmpRestart, restartButtonSize, restartButtonSize, false);
        restartButtonX = scrWidth / 2 + 2 * stdSize - restartButtonSize;
        restartButtonY = scrHeight / 2 - 2 * stdSize - 3 * restartButtonSize / 2;
    }

    public void initGame()  {
        interstitialShown = false;
        endGame = false;
        tileManager.initGame();
        tileManager.update();
        //endgameSprite = new EndGame(getResources(), scrWidth, scrHeight);
        score = new Score(getResources(), scrWidth, scrHeight, stdSize, getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE));
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread = new MainThread(surfaceHolder, this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        thread.setSurfaceHolder(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while (retry)   {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e)    {
                e.printStackTrace();
            }
        }
    }

    public void update()    {
        if (!endGame) {
            tileManager.update();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawRGB(255, 245, 250);
        grid.draw(canvas);
        tileManager.draw(canvas);
        score.draw(canvas);
        canvas.drawBitmap(restartButton, restartButtonX, restartButtonY, null);
        if (endGame)    {
            endgameSprite.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (endGame)    {
            if (event.getAction() == MotionEvent.ACTION_DOWN)   {
                initGame();
            }
        } else {
            float eventX = event.getAxisValue(MotionEvent.AXIS_X);
            float eventY = event.getAxisValue(MotionEvent.AXIS_Y);
            if (event.getAction() == MotionEvent.ACTION_DOWN &&
                eventX > restartButtonX && eventX < restartButtonX + restartButtonSize &&
                eventY > restartButtonY && eventY < restartButtonY + restartButtonSize)   {
                initGame();
            } else {
                swipe.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onSwipe(Direction direction) {
        tileManager.onSwipe(direction);
    }

    @Override
    public void gameOver() {
        endGame = true;
        if (endGame && !interstitialShown && !isAdsFree)  {
            interstitialShown = true;
            loadInterstitialAd();
        }
    }

    @Override
    public void updateScore(int delta) {
        score.updateScore(delta);
    }

    @Override
    public void reached2048() {
        score.reached2048();
    }

    public void loadInterstitialAd()    {
        if (!isAdsFree) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            interstitialAd.show();
                        }
                    });
                }
            });
        }
    }
}
