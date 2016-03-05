package edu.up.Pong_losh18;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

/**
 * @author Kieran Losh
 * @version 2/25/2016.
 */
public class PongBall {
    //location of the ball
    private float x;
    private float y;
    //angle in degrees
    private float angle;
    //speed in px/tick
    private float speed;
    //radius
    private float radius;




    //color of the ball
    private int color;



    /**
     * Default constructor
     */
    public PongBall(){
        x = 0;
        y = 0;
        angle = 0;
        speed = 0;
        radius = 15.0f;
        color = Color.WHITE;
    }

    /**
     * PongBall constructor with initial values
     * @param x x position
     * @param y y position
     * @param angle angle in degrees, according to unit circle
     * @param speed speed in px/tick
     * @param color color of the ball
     */
    public PongBall(float x, float y, float radius, float angle, float speed, int color){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.angle = angle;
        this.speed = speed;
        this.color = color;
    }

    public void tick(){
        x += speed*Math.cos(angle*Math.PI/180.0f);
        y += speed*Math.sin(angle*Math.PI/180.0f);
    }

    /**
     * draw the ball on the canvas
     * @param canvas Canvas to draw on
     */
    public void draw(Canvas canvas)
    {
        Paint p = new Paint();
        p.setColor(color);
        canvas.drawCircle(x, y, radius, p);
    }
    /**
     * Returns if
     * @param left left edge of object to check
     * @param top   top edge of object to check
     * @param right right edge of object to check
     * @param bottom bottom edge of object to check
     * @return flase if no intersection, true if intersects
     */
    public boolean intersects(float left, float top, float right, float bottom){
        if (x + radius >= left && x - radius <= right ){
            if (y + radius >= top && y - radius <= bottom){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the ball intersects a rectangle
     * @param r Rectangle to check
     * @return true if intersects, false if does not
     */
    public boolean intersects(Rect r){
        float left = r.centerX() - r.width()/2.0f;
        float right = r.centerX() + r.width()/2.0f;
        float top = r.centerY() - r.height()/2.0f;
        float bottom = r.centerY() + r.height()/2.0f;
        if (x + radius >= left && x - radius <= right ){
            if (y + radius >= top && y - radius <= bottom){
                return true;
            }
        }
        return false;
    }

    /**
     * Getters and setters
     * for each variable
     */

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
