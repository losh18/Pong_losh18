package edu.up.Pong_losh18;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Pong animator class animates and simulates balls boucing around in the pong game
 * @author Kieran Losh
 * @version 2/25/2016.
 */
public class PongAnimator implements Animator, View.OnTouchListener, View.OnClickListener, GestureDetector.OnGestureListener {

    //buffer around the screen which corresponds to the walls on the top, bottom,
    // and left side of the screen
    private float buffer = 50.0f;
    //paddle height
    private float paddleHeight = 500.0f;
    private ArrayList<PongBall> balls = new ArrayList<>();

    //keeps track of whether this is the first frame of the animation or not
    private boolean start = true;

    //rectangles on the playing area
    //they serve as boundaries and a paddle
    private Rect top;
    private Rect left;
    private Rect bottom;
    private Rect right;
    private Rect paddle;
    private Rect paddleHitBox;
    private Rect paddleArea; //area in which the paddle can move
    private float speed; //speed of the paddle

    //instance variables that do not need to be re-init'd
    //every single time tick is called
    float width;
    float height;
    float lBorder;
    float tBorder;
    float bBorder;
    float rBorder;
    MainActivity m;
    GestureDetector gDetect;

    private Random rand;


    public PongAnimator(){};

    public PongAnimator(MainActivity m){
        this.m = m;
        gDetect = new GestureDetector(m, this);
    }

    @Override
    public int interval() {
        return 17;
    }

    @Override
    public int backgroundColor() {
        return Color.BLACK;
    }

    @Override
    public boolean doPause() {
        return false;
    }

    @Override
    public boolean doQuit() {
        return false;
    }

    @Override
    public void tick(Canvas canvas) {
        //if this is the first runthrough, initialize objects
        if (start) {
            rand = new Random();
            //important variables for drawing
            width = canvas.getWidth();
            height = canvas.getHeight();
            lBorder = buffer;
            tBorder = buffer;
            bBorder = height - buffer;
            rBorder = width - buffer;
            //create rectangles for each of the sides and the paddle
            top = new Rect(-500, -500, (int) width, (int) tBorder);
            left = new Rect(-500, -500, (int) lBorder, (int) height);
            bottom = new Rect(-500, (int) bBorder, (int) width, (int) height + 500);
            right = new Rect((int) width, 0, (int) width + 500, (int) height);
            paddle = new Rect((int) (width - buffer), (int) (height / 2 - paddleHeight / 2), (int) (width), (int) (height / 2 + paddleHeight / 2.0f));
            paddleHitBox = new Rect((int) (width - buffer), (int) (height / 2 - paddleHeight / 2), (int) (width) + 500, (int) (height / 2 + paddleHeight / 2.0f));
            paddleArea = new Rect((int)(width - 7*buffer), (int)(buffer), (int)width, (int)(height - buffer));
            if (balls.size() == 0 ) {
                addBall();
            }
            start = false;
        }

        //Draw all the things!
        Paint objPaint = new Paint();
        Paint greyPaint = new Paint();
        greyPaint.setColor(0xFF105010);
        objPaint.setColor(Color.GREEN);
        objPaint.setTextSize(50.0f);
        canvas.drawRect(top, objPaint);
        canvas.drawRect(left, objPaint);
        canvas.drawRect(bottom, objPaint);
        canvas.drawRect(paddleArea, greyPaint);
        canvas.drawRect(paddle, objPaint);

        //iterate through the list of balls and check for intersections
        for (int i = 0; i < balls.size(); i++){
            PongBall b = balls.get(i);
            b.tick();
            //split angle into components to more easily reverse
            double x = Math.cos(b.getAngle()*Math.PI/180.0);
            double y = Math.sin(b.getAngle()*Math.PI/180.0);
            //check to see if
            if (b.intersects(top)){
                //reverse y direction when hitting the top
                if (y < 0) {
                    y *= -1;
                    //add some randomness to the angle
                    x += (Math.random() - .5) / 3.0f;
                }
            }
            if (b.intersects(left)){
                if (x < 0) {
                    //reverse the x direction when hitting the side
                    x *= -1;
                    //add some randomness to the angle
                    y += (Math.random() - .5) / 3.0f;
                }
            }
            if (b.intersects(bottom)){
                if (y > 0) {
                    //reverse y direction when hitting the top
                    y *= -1;
                    //add some randomness to the angle
                    x += (Math.random() - .5) / 3.0f;
                }
            }
            if (b.intersects(paddleHitBox)){
                //reverse the x direction when hitting the paddle
                if (x > 0){
                    x *= -1;
                    //the ball should be going as fast as the paddle at least
                    b.setSpeed(Math.max(speed, b.getSpeed()));
                    //add some randomness to the angle
                    y += (Math.random() - .5)/3.0f;
                }
            }
            //if the ball intersects the right side, remove it
            if (b.intersects(right)){
                balls.remove(i);
                i--; //we need to account for the fact an element was removed from the arraylist
            }
            else {
                //reconstruct the angle from the components
                if (x >= 0) { //since the range of arctan is only in +x, we have to account for -x as well
                    b.setAngle((float) (Math.atan(y / x) * 180.0f / Math.PI));
                }
                else{
                    b.setAngle((180.0f  + (float) (Math.atan(y / x) * 180.0f / Math.PI)));
                }
                //draw the ball
                b.draw(canvas);
            }
        }

        //display message about continuing when there are not balls left
        if (balls.isEmpty()){
            canvas.drawText("Tap when you are ready to continue", 2*buffer, height/2.0f, objPaint);
        }
    }

    /**
     * Adds a ball to the game, with random position and random velocity
     */
    private void addBall(){
        float radius = 30.0f;
        balls.add(new PongBall(rand.nextFloat()*(rBorder - lBorder - radius)+lBorder+radius,
                rand.nextFloat()*(bBorder - tBorder - radius)+tBorder+radius,
                radius,
                rand.nextFloat()*360.0f,
                rand.nextFloat()*30.0f+ 20.0f,
                Color.rgb(150,255,150)));
    }

    @Override
    public void onTouch(MotionEvent event) {
        //when the ball goes off the screen, allow the user to tap and create another ball
        if (event.getAction() == MotionEvent.ACTION_DOWN && balls.size() == 0 &&
                event.getX() < width - paddleArea.width()) {
            addBall();
        }
        gDetect.onTouchEvent(event);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gDetect.onTouchEvent(motionEvent);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.expertModeButton:
                paddleHeight = 250.0f;
                start = true;
                break;
            case R.id.beginnerModeButton:
                paddleHeight = 500.0f;
                start = true;
                break;
            default:
                //This button doesn't exist. How did you even get pressed?
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    /**
     External Citation
     Date: 3/4/2016
     Problem: I didn't have the gesturedetector specifics in my notes
     Resource: Example from lecture on Doodads
     Solution: I looked at how Nuxoll did it in the lecture via the code posted on moodle.
     */
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //the following code detects scrolls that move the paddle and adjusts the paddle as it is dragged
        float right = 0.0f;
        float top = 0.0f;
        float left = 0.0f;
        float bottom = 0.0f;
        speed = Math.abs(motionEvent1.getX() - motionEvent.getX())/2.0f;
        if (motionEvent1.getX() >= width - paddleArea.width() //if the touch is on the paddle movement
                && motionEvent1.getY() >= paddle.height()/2.0f - paddle.centerY()
                && motionEvent1.getY() <= paddle.centerY() + paddle.height()/2.0f){
            if (motionEvent1.getY() <= buffer + paddle.height()/2.0f){ //don't move the paddle up into the top barrier
                top = buffer;
                bottom = buffer + paddle.height();
            }
            else if (motionEvent1.getY() >= height - paddle.height()/2.0f - buffer) {//don't move the paddle below the bottom barrier
                top = height - buffer - paddle.height();
                bottom = height - buffer;
            }
            else { //move the paddle freely!
                top = motionEvent1.getY() - paddle.height()/2.0f;
                bottom = motionEvent1.getY() + paddle.height()/2.0f;
            }
            if (motionEvent1.getX() < width - paddleArea.width() + paddle.width()/2.0f){//don't move the paddle outside the box to the left
                left = width - paddleArea.width();
                right = width - paddleArea.width() + paddle.width();
            }
            else if (motionEvent1.getX() > width - paddle.width()/2.0f){//don't move the paddle outside the box to the right
                left = width - paddle.width();
                right = width;
            }
            else {
                left = motionEvent1.getX() - paddle.width()/2.0f;
                right = motionEvent1.getX() + paddle.width()/2.0f;
            }
            //set the new paddle dimensions and hitbox
            paddle.set((int)left, (int)top, (int)right, (int)bottom);
            paddleHitBox.set((int)left, (int)top, (int)right+500, (int)bottom);
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
