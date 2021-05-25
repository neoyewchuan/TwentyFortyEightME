package com.thefatoldman.twentyfortyeightme;

public interface GameManagerCallback {
    void gameOver();
    void updateScore(int delta);
    void reached2048();
}
