package com.chandrachud.vanish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class ChooseSignup extends AppCompatActivity {

    private ImageView introImage;
    private Button signupButton;
    private Button loginButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_signup);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            Intent intent = new Intent(ChooseSignup.this, MainActivity.class);
            startActivity(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(ChooseSignup.this,R.color.white));// set status background white

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }

        findViewsById();
        setOnClickListeners();
    }

    private void findViewsById()
    {
        introImage = findViewById(R.id.login_or_signup_image);

        introImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    introImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    introImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screen_width = displayMetrics.widthPixels;

                introImage.getLayoutParams().width = screen_width;
                int newHeight = (int) (screen_width*0.8);

                introImage.getLayoutParams().height = newHeight;



            }
        });





        introImage.setImageResource(R.drawable.login_signup_choose);


        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
    }

    private void setOnClickListeners()
    {

        PushDownAnim.setPushDownAnimTo(loginButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       changeActivity(LoginActivity.class);

                    }
                });

        PushDownAnim.setPushDownAnimTo(signupButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        changeActivity(SignupActivity.class);

                    }
                });



    }

    private void changeActivity(Class classObject)
    {
        final Intent intent = new Intent(ChooseSignup.this, classObject);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(intent);

            }
        }, 1000);
    }
}