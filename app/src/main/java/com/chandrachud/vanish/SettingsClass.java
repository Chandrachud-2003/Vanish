package com.chandrachud.vanish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bitvale.switcher.SwitcherX;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class SettingsClass extends AppCompatActivity {

    private int screenWidth;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private int profilePicNum;
    private boolean isPremium;
    private boolean isNotify;
    private String name;
    private String phone;

    private ImageView profilePicture;
    private TextView nameText;
    private TextView phoneText;
    private ImageButton closeButton;

    private AnimatedBottomBar mAnimatedBottomBar;

    private MaterialRippleLayout notifyLayout;
    private ImageView notifyImage;
    private SwitcherX notifySwitcher;

    private MaterialRippleLayout premiumLayout;
    private ImageView premiumImage;
    private SwitcherX premiumSwitcher;

    private MaterialRippleLayout accountLayout;
    private ImageView accountImage;
    private ImageView accountArrow;

    private MaterialRippleLayout signoutLayout;
    private ImageView signoutImage;
    private ImageView signoutArrow;

    private MaterialRippleLayout securityLayout;
    private ImageView securityImage;
    private ImageView securityArrow;

    private MaterialRippleLayout contactLayout;
    private ImageView contactImage;
    private ImageView contactArrow;

    private int[] profilePics = Constants.profilePics_medium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_class);

        assignScreenSizes();
        findViewsById();
    }

    private void getSharedPrefsInfo()
    {
        name = mSharedPreferences.getString(Constants.name_prefs, "");
        nameText.setText(name);
        playAnimationOnView(nameText, Techniques.Landing);
        phone = mSharedPreferences.getString(Constants.ccc_prefs, "")+mSharedPreferences.getString(Constants.phone_prefs, "");
        phoneText.setText(phone);
        playAnimationOnView(phoneText, Techniques.Landing);
        profilePicNum = mSharedPreferences.getInt(Constants.profilePicNum_prefs, 0);
        profilePicture.setImageResource(profilePics[profilePicNum]);
        playAnimationOnView(profilePicture, Techniques.Landing);
        isPremium = mSharedPreferences.getBoolean(Constants.premium_prefs, false);
        if(isPremium)
        {
            premiumSwitcher.setChecked(true, true);
            premiumSwitcher.setClickable(false);

        }
        else {
            premiumSwitcher.setChecked(false, true);
            premiumSwitcher.setClickable(true);
        }

        boolean isNotify = mSharedPreferences.getBoolean(Constants.notify_settings_pref, false);
        if (isNotify)
        {
            notifySwitcher.setChecked(true, true);
        }
        else {
            notifySwitcher.setChecked(false, true);

        }

        playAnimationOnView(notifyLayout, Techniques.Landing);
        playAnimationOnView(premiumLayout, Techniques.Landing);
        playAnimationOnView(accountLayout, Techniques.Landing);
        playAnimationOnView(securityLayout, Techniques.Landing);
        playAnimationOnView(contactLayout, Techniques.Landing);
        playAnimationOnView(signoutLayout, Techniques.Landing);
    }

    private void assignScreenSizes()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = (int) (displayMetrics.widthPixels);
    }

    private void findViewsById()
    {
        mSharedPreferences = getSharedPreferences(Constants.shared_prefs, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        profilePicture = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.nameText);
        phoneText = findViewById(R.id.phoneText);
        closeButton = findViewById(R.id.closeButton);
        notifyLayout = findViewById(R.id.notificationsLayout);
        notifyImage = findViewById(R.id.notificationsImage);
        notifySwitcher = findViewById(R.id.notificationsSwitcher);
        premiumLayout = findViewById(R.id.premiumLayout);
        premiumImage = findViewById(R.id.premiumImage);
        premiumSwitcher = findViewById(R.id.premiumSwitcher);
        accountLayout = findViewById(R.id.accountLayout);
        accountImage = findViewById(R.id.accountImage);
        accountArrow = findViewById(R.id.accountArrow);
        securityLayout = findViewById(R.id.securityLayout);
        securityImage = findViewById(R.id.securityImage);
        securityArrow = findViewById(R.id.securityArrow);
        contactLayout = findViewById(R.id.contactLayout);
        contactImage = findViewById(R.id.contactImage);
        contactArrow = findViewById(R.id.contactArrow);
        signoutLayout = findViewById(R.id.signoutLayout);
        signoutArrow = findViewById(R.id.signoutArrow);
        signoutImage = findViewById(R.id.signoutImage);

        mAnimatedBottomBar = findViewById(R.id.bottom_bar);
        mAnimatedBottomBar.selectTabAt(3, true);

        closeButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                closeButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.0729);

                closeButton.getLayoutParams().width = newDimen;

                closeButton.getLayoutParams().height = newDimen;
            }
        });

        profilePicture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                profilePicture.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.208);

                profilePicture.getLayoutParams().width = newDimen;

                profilePicture.getLayoutParams().height = newDimen;
            }
        });

        notifyImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                notifyImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.076);

                notifyImage.getLayoutParams().width = newDimen;

                notifyImage.getLayoutParams().height = newDimen;
            }
        });

        premiumImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                premiumImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.076);

                premiumImage.getLayoutParams().width = newDimen;

                premiumImage.getLayoutParams().height = newDimen;
            }
        });

        accountImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                accountImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.076);

                accountImage.getLayoutParams().width = newDimen;

                accountImage.getLayoutParams().height = newDimen;
            }
        });

        securityImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                securityImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.076);

                securityImage.getLayoutParams().width = newDimen;

                securityImage.getLayoutParams().height = newDimen;
            }
        });

        contactImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contactImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.076);

                contactImage.getLayoutParams().width = newDimen;

                contactImage.getLayoutParams().height = newDimen;
            }
        });

        signoutImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                signoutImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.076);

                signoutImage.getLayoutParams().width = newDimen;

                signoutImage.getLayoutParams().height = newDimen;
            }
        });

        accountArrow.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                accountArrow.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.052);

                accountArrow.getLayoutParams().width = newDimen;

                accountArrow.getLayoutParams().height = newDimen;
            }
        });

        securityArrow.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                securityArrow.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.052);

                securityArrow.getLayoutParams().width = newDimen;

                securityArrow.getLayoutParams().height = newDimen;
            }
        });

        contactArrow.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contactArrow.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.052);

                contactArrow.getLayoutParams().width = newDimen;

                contactArrow.getLayoutParams().height = newDimen;
            }
        });

        signoutArrow.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                signoutArrow.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int newDimen = (int) (screenWidth*0.052);

                signoutArrow.getLayoutParams().width = newDimen;

                signoutArrow.getLayoutParams().height = newDimen;
            }
        });

        setOnClickListeners();

        getSharedPrefsInfo();

    }

    private void setOnClickListeners()
    {
        PushDownAnim.setPushDownAnimTo(closeButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder signoutBuilder = new AlertDialog.Builder(SettingsClass.this);
                        signoutBuilder.setMessage("Are your sure you want to Close Vanish?");
                        signoutBuilder.setCancelable(true);
                        signoutBuilder.setTitle("Close Vanish");
                        signoutBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                finishAffinity();

                            }
                        });

                        signoutBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        AlertDialog signoutDialog = signoutBuilder.create();
                        signoutDialog.show();


                    }
                });

        PushDownAnim.setPushDownAnimTo(accountLayout)
                .setScale(PushDownAnim.MODE_SCALE, 0.9f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });

        PushDownAnim.setPushDownAnimTo(contactLayout)
                .setScale(PushDownAnim.MODE_SCALE, 0.9f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });

        PushDownAnim.setPushDownAnimTo(signoutLayout)
                .setScale(PushDownAnim.MODE_SCALE, 0.9f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder signoutBuilder = new AlertDialog.Builder(SettingsClass.this);
                        signoutBuilder.setMessage("Are your sure you want to Sign Out?");
                        signoutBuilder.setCancelable(true);
                        signoutBuilder.setTitle("Sign Out");
                        signoutBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SettingsClass.this, ChooseSignup.class);
                                startActivity(intent);
                                finish();

                            }
                        });

                        signoutBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        AlertDialog signoutDialog = signoutBuilder.create();
                        signoutDialog.show();
                    }
                });

        PushDownAnim.setPushDownAnimTo(securityLayout)
                .setScale(PushDownAnim.MODE_SCALE, 0.9f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });

        mAnimatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {

                if (i1!=3)
                {
                    switch (i1)
                    {
                        case 0:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(SettingsClass.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case 1:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(SettingsClass.this, ContactActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case 2:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(SettingsClass.this, ProfileClass.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                    }
                }

            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });

        notifySwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (notifySwitcher.isChecked()) {

                    isNotify = true;
                    mEditor.putBoolean(Constants.notify_settings_pref, true).apply();

                }

                else {

                    isNotify = false;
                    mEditor.putBoolean(Constants.notify_settings_pref, false).apply();
                }


            }
        });

        premiumSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (premiumSwitcher.isChecked()) {

                    isPremium = true;

                }

                else {

                    isPremium = false;
                }


            }
        });

        notifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNotify)
                {
                    notifySwitcher.setChecked(false, true);
                    isNotify = false;
                    mEditor.putBoolean(Constants.notify_settings_pref, false).apply();

                }

                else {
                    notifySwitcher.setChecked(true, true);
                    isNotify = true;
                    mEditor.putBoolean(Constants.notify_settings_pref, true).apply();

                }

            }
        });

        premiumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPremium)
                {
                    premiumSwitcher.setChecked(false, true);
                    isPremium = false;

                }

                else {
                    premiumSwitcher.setChecked(true, true);
                    isPremium = true;

                }

            }
        });

    }

    private void playAnimationOnView(View v, Techniques animation)
    {
        YoYo.with(animation)
                .duration(2000)
                .repeat(0)
                .playOn(v);

    }
}