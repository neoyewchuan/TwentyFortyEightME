package com.thefatoldman.twentyfortyeightme.sprites;

import android.graphics.Canvas;

import com.thefatoldman.twentyfortyeightme.TileManagerCallback;

import java.util.Random;

public class Tile implements Sprite {

    private int standardSize, screenWidth, screenHeight;
    private TileManagerCallback callback;
    private int count = 1;
    private int currentX, destX;
    private int currentY, destY;
    private boolean moving = false;
    private int speed = 200;
    private boolean increment;

    public Tile(int standardSize, int screenWidth, int screenHeight, TileManagerCallback callback, int matrixX, int matrixY, int count)   {
        this(standardSize, screenWidth, screenHeight, callback, matrixX, matrixY);
        this.count = count;
    }
    public Tile(int standardSize, int screenWidth, int screenHeight, TileManagerCallback callback, int matrixX, int matrixY)   {
        this.standardSize = standardSize;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.callback = callback;
        this.currentX = this.destX = screenWidth / 2 - 2 * standardSize  + matrixY * standardSize;
        this.currentY = this.destY = screenHeight / 2 - 2 * standardSize + matrixX * standardSize;
        int chance = new Random().nextInt(100);
        if (chance >= 90)   {
            count = 2;
        }
    }

    public void move(int matrixX, int matrixY)  {
        this.moving = true;
        this.destX = screenWidth / 2 - 2 * standardSize  + matrixY * standardSize;
        this.destY = screenHeight / 2 - 2 * standardSize + matrixX * standardSize;
    }

    public int getValue()   {
        return count;
    }

    public Tile increment() {
        increment = true;
        return this;
    }

    public boolean toIncrement()    {
        return increment;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(callback.getBitmap(count), this.currentX, this.currentY, null);
        if (this.moving && this.currentX == this.destX && this.currentY == this.destY)    {
            this.moving = false;
            if (increment)  {
                count++;
                increment = false;
                int amount = (int) Math.pow(2, count);
                callback.updateScore(amount);
                if (count == 11)    {
                    callback.reached2048();
                }
            }
            callback.finishedMoving(this);
        }
    }

    @Override
    public void update() {
        if (this.currentX < this.destX) {
            if (this.currentX + this.speed > this.destX) {
                this.currentX = this.destX;
            } else {
                this.currentX += this.speed;
            }
        } else if (this.currentX > this.destX) {
            if (this.currentX - this.speed < this.destX)    {
                this.currentX = this.destX;
            } else {
                this.currentX -= this.speed;
            }
        }

        if (this.currentY < this.destY) {
            if (this.currentY + this.speed > this.destY)    {
                this.currentY = this.destY;
            } else {
                this.currentY += this.speed;
            }
        } else if (this.currentY > this.destY)  {
            if (this.currentY -  this.speed < this.destY)   {
                this.currentY = this.destY;
            } else {
                this.currentY -= this.speed;
            }
        }

    }


}
