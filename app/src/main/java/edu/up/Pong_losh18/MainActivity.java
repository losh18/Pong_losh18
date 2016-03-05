package edu.up.Pong_losh18;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.TextView;

/**
 * MainActivity
 *
 * This is the activity for the Pong game. It attaches a PongAnimator to
 * an AnimationSurface.
 *
 * @author Kieran Losh
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @version Feb 2016
 *
 * Note: This game is meant to be played in landscape mode
 * Enhancements (Part A):
 * [10 points] Allow an arbitrary number of balls to be in play at once. Additional balls can be created
 * by user control (a button press)
 * [5 points] When a ball leaves the field of play, don't add a new ball until the user indicates she is
 * ready by tapping the screen (the remove button is included for convenience of testing this)
 * [5 points] Allow the user to change the size of the paddle (for “beginner” vs. “expert” mode) via
 * buttons on the bottom of the screen
 *
 * Enhancements (Part B):
 * [20 Points] The paddle can be moved in both the X and Y direction, and the ball's speed is increased
 * if the paddle is moved towards it when it impacts.
 * [5 Points] The bouncing has a small amount of randomness in it.
 *
 */
public class MainActivity extends Activity {

    /**
     * creates an AnimationSurface containing a TestAnimator.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect the animation surface with the animator
        PongAnimator pong = new PongAnimator(this);
        Button expertButton = (Button)findViewById(R.id.expertModeButton);
        Button beginnerButton = (Button)findViewById(R.id.beginnerModeButton);
        AnimationSurface mySurface = (AnimationSurface) this
                .findViewById(R.id.animationSurface);
        mySurface.setAnimator(pong);
        expertButton.setOnClickListener(pong);
        beginnerButton.setOnClickListener(pong);
        //lock screen to landscape mode
        /**
         External Citation
         Date: 3/4/2016
         Problem: I didn't remember how to lock the screen orientation to landscape
         Resource: Example from lecture on Doodads
         Solution: I looked at how Nuxoll did it in the lecture via the code posted on Moodle.
         */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
