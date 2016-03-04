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
            top = new Rect(0, 0, (int) width, (int) tBorder);
            left = new Rect(0, 0, (int) lBorder, (int) height);
            bottom = new Rect(0, (int) bBorder, (int) width, (int) height);
            right = new Rect((int) width, 0, (int) width + 100, (int) height);
            paddle = new Rect((int) (width - buffer), (int) (height / 2 - paddleHeight / 2), (int) (width), (int) (height / 2 + paddleHeight / 2.0f));
            if (balls.size() == 0 ) {
                addBall();
            }
            start = false;
        }
        //iterate through the list of balls
        for (int i = 0; i < balls.size(); i++){
            PongBall b = balls.get(i);
            b.tick();
            //split angle into components to more easily reverse
            double x = Math.cos(b.getAngle()*Math.PI/180.0);
            double y = Math.sin(b.getAngle()*Math.PI/180.0);
            //check to see if
            if (b.intersects(top)){
                //reverse y direction when hitting the top
                y *= -1;
            }
            if (b.intersects(left)){
                //reverse the x direction when hitting the side
                x *= -1;
            }
            if (b.intersects(bottom)){
                //reverse y direction when hitting the top
                y *= -1;
            }
            if (b.intersects(paddle)){
                //reverse the x direction when hitting the paddle
                x *= -1;
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
        //Draw all the things!
        Paint objPaint = new Paint();
        objPaint.setColor(Color.GREEN);
        objPaint.setTextSize(50.0f);
        canvas.drawRect(top, objPaint);
        canvas.drawRect(left, objPaint);
        canvas.drawRect(bottom, objPaint);
        canvas.drawRect(paddle, objPaint);
        //display message about continuing when there are not balls left
        if (balls.isEmpty()){
            canvas.drawText("Tap when you are ready to continue", width/2.0f, height/2.0f, objPaint);
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
                rand.nextFloat()*20.0f+ 20.0f,
                Color.rgb(150,255,150)));
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && balls.size() == 0 &&
                event.getX() < width - buffer - 15.0f) {
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

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if (motionEvent1.getX() >= width - 2*buffer //if the touch is on the paddle (or close)
                && motionEvent1.getY() >= paddle.height()/2.0f - paddle.centerY()
                && motionEvent1.getY() <= paddle.centerY() + paddle.height()/2.0f){
            if (motionEvent1.getY() <= buffer + paddle.height()/2.0f){ //don't move the paddle up into the top barrier
                paddle.set((int)(width - buffer), (int)(buffer),(int) width,(int)( buffer + paddle.height()));
            }
            else if (motionEvent1.getY() >= height - paddle.height()/2.0f - buffer) {//dont' move the paddle below the bottom barrier
                paddle.set((int)(width - buffer), (int)(height - buffer  - paddle.height()),(int) width,(int)(height - buffer));
            }
            else { //move the paddle!
                paddle.set((int)(width - buffer), (int)(motionEvent1.getY() - paddle.height()/2.0f),(int) width,(int)(motionEvent1.getY() + paddle.height()/2.0f));
            }
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
