package com.chandrachud.vanish;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private LottieAnimationView splashAnim;
    private TextView splashText;

    private int screenWidth;
    private int counter, limit;
    private boolean continueAnim;
    private boolean finishedAnim;
    private boolean signedIn;
    private boolean enteredCheck;

    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        assignScreenSize();
        findViewsById();
        setListeners();
    }

    private void assignScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = (int) (displayMetrics.widthPixels);
    }

    private void findViewsById()
    {
        continueAnim = false;
        counter = 0;
        limit = 2;
        enteredCheck = false;


        splashAnim = findViewById(R.id.splashAnim);
        splashText = findViewById(R.id.splashText);

        splashAnim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    splashAnim.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    splashAnim.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth*0.7);

                splashAnim.getLayoutParams().width = newDimensions;
                splashAnim.getLayoutParams().height = newDimensions;

                splashAnim.requestLayout();

                checkSignedIn();


            }
        });





    }

    private void setListeners()
    {
        splashAnim.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Log.d("TAG", "onAnimationUpdate: "+valueAnimator.getAnimatedValue());
                if (valueAnimator.getAnimatedFraction()>0.95d && !continueAnim)
                {
                    counter+=1;
                    continueAnim = true;
                }

                else if (valueAnimator.getAnimatedFraction()<0.95d && continueAnim){

                    if (counter>=limit) {
                        splashAnim.cancelAnimation();
                        finishedAnim = true;

                        if (enteredCheck)
                        {
                            Intent intent;

                            if (signedIn)
                            {
                                Toast.makeText(SplashScreen.this, "Setting up your Account...", Toast.LENGTH_LONG).show();

                                intent = new Intent(SplashScreen.this, MainActivity.class);
                            }

                            else {
                                Toast.makeText(SplashScreen.this, "Welcome to Vanish!", Toast.LENGTH_LONG).show();

                                intent = new Intent(SplashScreen.this, ChooseSignup.class);
                            }

                            startActivity(intent);
                            finish();
                        }



                    }

                    continueAnim = false;

                }
            }
        });
    }

    private void checkSignedIn()
    {
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser==null)
        {
            enteredCheck = true;
            signedIn = false;
        }
        else {
            enteredCheck = true;
            signedIn = true;
        }

        if (finishedAnim)
        {
            Intent intent;

            if (signedIn)
            {
                intent = new Intent(SplashScreen.this, MainActivity.class);
            }

            else {
                intent = new Intent(SplashScreen.this, ChooseSignup.class);
            }

            startActivity(intent);
            finish();
        }
    }


}