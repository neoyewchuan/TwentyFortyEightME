package com.thefatoldman.twentyfortyeightme;

import android.graphics.Bitmap;

import com.thefatoldman.twentyfortyeightme.sprites.Tile;

public interface TileManagerCallback {
    Bitmap getBitmap(int count);
    void finishedMoving(Tile tile);
    void updateScore(int delta);
    void reached2048();
}
