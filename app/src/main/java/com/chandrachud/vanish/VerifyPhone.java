package com.chandrachud.vanish;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.chandrachud.vanish.fragments.BottomSheetProfileFragment;
import com.chandrachud.vanish.helperClasses.CreateFirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pd.chocobar.ChocoBar;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import in.goodiebag.carouselpicker.CarouselPicker;

public class VerifyPhone extends AppCompatActivity implements BottomSheetProfileFragment.ButtonClickListener, CreateFirebaseUser.signUpFinishedListener  {

    private Button continueButton;
    private ConstraintLayout googleLayout;
    private LottieAnimationView googleAnimation;
    private ImageView phoneImage;
    private ImageView googleImage;
    private TextView phoneText;
    private LottieAnimationView phoneAnimation;
    private ImageView verifyImage;
    private TextView infoText;
    private TextView googleText;
    private ImageButton backButton;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1001;
    private static final int PERMISSION_REQUEST_CODE = 1234;

    private FirebaseAuth mFirebaseAuth;

    private boolean googleSignInFinished;
    private boolean phoneCheckFinished;
    private boolean phoneFound;

    private String prevActivity;
    private String name;
    private String phoneNum;

    private AuthCredential mAuthCredential;

    private AlertDialog verifyDialog;
    private AlertDialog continueDialog;

    private ConstraintLayout bottomSheetLayout;
    private BottomSheetBehavior mBottomSheetBehavior;

    private int profilePicPosition;

    private BottomSheetProfileFragment mProfileFragment;

    private ArrayList<CarouselPicker.PickerItem> mPickerItems;

    private int[] profilePictures;
    private String onlyNum;
    private String onlyCcc;

    FirebaseAuthSettings firebaseAuthSettings;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(VerifyPhone.this,R.color.white));// set status background white

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }

        Intent intent = getIntent();
        prevActivity = intent.getStringExtra(Constants.activity_Intent);
        phoneNum = intent.getStringExtra(Constants.phone_Intent);
        if (prevActivity.equals(Constants.signup_Activity)) {
            name = intent.getStringExtra(Constants.nameSignup);
            onlyCcc = intent.getStringExtra(Constants.onlyCcc_intent );
            onlyNum = intent.getStringExtra(Constants.onlyNum_intent);
        }


        findViewsById();
        setOnClickListeners();


    }

    private void findViewsById() {
        phoneAnimation = findViewById(R.id.verifyPhoneAnimation);
        verifyImage = findViewById(R.id.verifyImage);
        googleAnimation = findViewById(R.id.googleAnimationView);
        phoneImage = findViewById(R.id.verifyPhoneImage);
        googleImage = findViewById(R.id.googleImage);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screen_width = displayMetrics.widthPixels;

        verifyImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    verifyImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    verifyImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                verifyImage.getLayoutParams().width = screen_width;
                int newHeight = (int) (screen_width * 0.8);

                verifyImage.getLayoutParams().height = newHeight;


            }
        });

        phoneAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    phoneAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    phoneAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screen_width * 0.1);
                phoneAnimation.getLayoutParams().width = newDimensions;


                phoneAnimation.getLayoutParams().height = newDimensions;


            }
        });

        googleAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    googleAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    googleAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screen_width * 0.1);
                googleAnimation.getLayoutParams().width = newDimensions;


                googleAnimation.getLayoutParams().height = newDimensions;


            }
        });

        phoneImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    phoneImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    phoneImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screen_width * 0.1);
                phoneImage.getLayoutParams().width = newDimensions;


                phoneImage.getLayoutParams().height = newDimensions;


            }
        });

        googleImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    googleImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    googleImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screen_width * 0.07);
                googleImage.getLayoutParams().width = newDimensions;


                googleImage.getLayoutParams().height = newDimensions;


            }
        });


        continueButton = findViewById(R.id.continueButton);
        continueButton.setText("Continue");
        googleLayout = findViewById(R.id.googleSignInConstraintLayout);
        phoneText = findViewById(R.id.verifyPhoneEditText);
        setPhoneText("Waiting...");
        setPhoneColorText(false);
        googleText = findViewById(R.id.googleText);
        googleText.setText("Sign-In with Google");
        infoText = findViewById(R.id.phoneInfoText);
        infoText.setText("Sign-In with Google first");
        backButton = findViewById(R.id.backButton);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mFirebaseAuth = FirebaseAuth.getInstance();

        googleSignInFinished = false;
        phoneCheckFinished = false;
        phoneFound = false;

        verifyDialog = new SpotsDialog.Builder().setContext(VerifyPhone.this).build();
        verifyDialog.setTitle("Verifying Your Phone Number");
        verifyDialog.setMessage("Please Wait...");

        continueDialog = new SpotsDialog.Builder().setContext(VerifyPhone.this).build();
        //continueDialog.setTitle("Setting up your Account");
        continueDialog.setMessage("Please Wait...");



        profilePicPosition = 0;

        profilePictures = Constants.profilePics_medium;

        mPickerItems = new ArrayList<>();
        for(int i=0;i<profilePictures.length;i++)
        {
            mPickerItems.add(new CarouselPicker.DrawableItem(profilePictures[i]));
        }

        mProfileFragment = new BottomSheetProfileFragment(screen_width, VerifyPhone.this, mPickerItems);

         firebaseAuthSettings = mFirebaseAuth.getFirebaseAuthSettings();
         firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+917022458655", "123456");


    }

    private void setOnClickListeners() {
        PushDownAnim.setPushDownAnimTo(googleLayout)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        signInToGoogle();


                    }
                });

        PushDownAnim.setPushDownAnimTo(backButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent();
                        if (prevActivity.equals(Constants.login_Activity))
                        {
                            intent = new Intent(VerifyPhone.this, LoginActivity.class);

                        }

                        else if (prevActivity.equals(Constants.signup_Activity))
                        {
                            intent = new Intent(VerifyPhone.this, SignupActivity.class);
                        }
                        startActivity(intent);


                    }
                });

        PushDownAnim.setPushDownAnimTo(continueButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (phoneCheckFinished&&googleSignInFinished)
                        {
                            Intent intent = new Intent();

                            if (!(phoneFound))
                            {
                                continueDialog.setTitle("Verifying Your Information");
                                continueDialog.show();

                                intent = new Intent(VerifyPhone.this, OtpActivity.class);
                                intent.putExtra(Constants.activity_Intent, prevActivity);
                                intent.putExtra(Constants.phone_Intent, phoneNum);
                                if (prevActivity.equals(Constants.signup_Activity))
                                {
                                    intent.putExtra(Constants.nameSignup, name);
                                    intent.putExtra(Constants.onlyNum_intent, onlyNum);
                                    intent.putExtra(Constants.onlyCcc_intent, onlyCcc);
                                }
                                intent.putExtra(Constants.auth_intent, mAuthCredential);
                                continueDialog.dismiss();
                                startActivity(intent);



                            }
                            else {


                                if (prevActivity.equals(Constants.signup_Activity))
                                {
                                    openModalBottomDialog();


                                }
                                else if (prevActivity.equals(Constants.login_Activity))
                                {
                                    continueDialog.setTitle("Verifying Your Information");
                                    continueDialog.show();




                                    continueDialog.dismiss();

                                    continueDialog.setTitle("Logging In");
                                    continueDialog.show();
                                    completeGoogleSignIn();

                                }
                            }







                        }

                        else if (!googleSignInFinished)
                        {
                            setUpChocoBar("Please Sign-In with your Google Account", R.color.chocoYellow, R.color.white, R.drawable.warning_snackbar);


                        }






                    }
                });


    }

    private void signInToGoogle() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(VerifyPhone.this, "Sign in succedded", Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                setUpChocoBar("Sign-up Failed, please try again", R.color.chocoRed, R.color.white, R.drawable.error_snackbar);
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        mAuthCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        googleSignInFinished = true;
        setInfoText("Verifying Phone Number");
        setCheckAnimationGoogle(true, true);
        Log.d("TAG", "firebaseAuthWithGoogle: Sign In finished");
        googleText.setText("Sign-In Finished");

        if (ActivityCompat.checkSelfPermission(VerifyPhone.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            requestPermission(Manifest.permission.READ_PHONE_STATE);

        } else {

            checkPhoneNumber();

        }


    }

    private void requestPermission(final String permission) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(VerifyPhone.this);
            builder.setTitle("Permission");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();

                    ActivityCompat.requestPermissions(VerifyPhone.this, new String[]{permission}, PERMISSION_REQUEST_CODE);


                }
            });

            builder.setMessage("We need access to Read your Phone State to verify your Number, you will be requested to Grant this Permission");

            AlertDialog alertDialog = builder.create();

            alertDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPhoneNumber();
                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(VerifyPhone.this);
                    builder.setTitle("Permission Request Denied");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent();
                            if (prevActivity.equals(Constants.signup_Activity)) {
                                intent = new Intent(VerifyPhone.this, SignupActivity.class);
                            } else if (prevActivity.equals(Constants.login_Activity)) {
                                intent = new Intent(VerifyPhone.this, LoginActivity.class);

                            }
                            dialogInterface.dismiss();
                            startActivity(intent);


                        }
                    });

                    builder.setMessage("We need access to Read your Phone State to verify your Number, you will be requested to Grant this Permission");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                }
                break;
        }
    }

    private void checkPhoneNumber() {
        verifyDialog.show();
        setLoadingAnimation(true, true);
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            verifyDialog.dismiss();
            if (phoneMgr.getLine1Number()!=null && phoneMgr.getLine1Number().equals(phoneNum))
            {
                setCheckAnimation(true, true);
                phoneFound = true;
                setPhoneColorText(true);
                setPhoneText(phoneNum);
                setInfoText("Number Verified");
                Log.d("TAG", "checkPhoneNumber: Stored number in phone: "+phoneMgr.getLine1Number());
                setUpChocoBar("Phone Number Verified", R.color.loginMain, R.color.white, R.drawable.tick_snackbar);
            }
            else {
                setCancelAnimation( true, true);
                phoneFound = false;
                setPhoneColorText(false);
                setPhoneText("Verification Failed");
                setInfoText("Unable to Verify Number, \nplease continue with OTP");
                Log.d("TAG", "checkPhoneNumber: "+phoneMgr.getLine1Number());
                setUpChocoBar("Unable to Auto-Verify Number, please proceed with OTP", R.color.chocoRed, R.color.white, R.drawable.error_snackbar);


            }

            phoneCheckFinished = true;

        }

        else {

            verifyDialog.dismiss();
            Log.d("TAG", "checkPhoneNumber: Permission not granted");
        }


    }

    private void setCheckAnimation(boolean check, boolean v)
    {
        if (check) {

            phoneAnimation.setAnimation(R.raw.phone_confirmed);
            phoneAnimation.setVisibility(View.VISIBLE);
            phoneAnimation.playAnimation();
            phoneAnimation.setRepeatCount(0);

        }

        else {

            phoneAnimation.cancelAnimation();

            if (v)
            {
                phoneAnimation.setVisibility(View.VISIBLE);
            }
            else {
                phoneAnimation.setVisibility(View.GONE);
            }


        }


    }

    private void setCancelAnimation(boolean check, boolean v)
    {
        if (check) {

            phoneAnimation.setAnimation(R.raw.login_fail);
            phoneAnimation.setVisibility(View.VISIBLE);
            phoneAnimation.playAnimation();
            phoneAnimation.setRepeatCount(0);

        }

        else {

            phoneAnimation.cancelAnimation();

            if (v)
            {
                phoneAnimation.setVisibility(View.VISIBLE);
            }
            else {
                phoneAnimation.setVisibility(View.GONE);
            }


        }


    }

    private void setLoadingAnimation(boolean check, boolean v)
    {
        if (check) {

            phoneAnimation.setAnimation(R.raw.loading_login);
            phoneAnimation.setVisibility(View.VISIBLE);
            phoneAnimation.playAnimation();
            phoneAnimation.setRepeatMode(LottieDrawable.RESTART);
            phoneAnimation.setRepeatCount(LottieDrawable.INFINITE);

        }

        else {

            phoneAnimation.cancelAnimation();

            if (v)
            {
                phoneAnimation.setVisibility(View.VISIBLE);
            }
            else {
                phoneAnimation.setVisibility(View.GONE);
            }


        }
    }

    private void setInfoText(String text)
    {
        infoText.setText(text);
    }

    private void setPhoneText(String text)
    {
        phoneText.setText(text);
    }

    private void setPhoneColorText(boolean color)
    {
        if (color)
        {
            phoneText.setTextColor(getResources().getColor(R.color.loginMain));
        }
        else {
            phoneText.setTextColor(getResources().getColor(R.color.black));

        }
    }

    private void setCheckAnimationGoogle(boolean check, boolean v)
    {
        if (check) {

            googleAnimation.setAnimation(R.raw.phone_confirmed);
            googleAnimation.setVisibility(View.VISIBLE);
            googleAnimation.playAnimation();
            googleAnimation.setRepeatCount(0);

        }

        else {

            googleAnimation.cancelAnimation();

            if (v)
            {
                googleAnimation.setVisibility(View.VISIBLE);
            }
            else {
                googleAnimation.setVisibility(View.GONE);
            }


        }


    }


    @Override
    public void onBottomSheetProfileButtonClicked(boolean type) {

        if(type)
        {
            continueDialog.setTitle("Setting up Your Account");
            continueDialog.show();
            completeGoogleSignIn();

        }

    }

    @Override
    public void onProfilePictureSelected(int position) {

        profilePicPosition = position;

    }

    private void openModalBottomDialog()
    {
        mProfileFragment.show(getSupportFragmentManager(), "BottomSheetProfileFragment");
    }

    private void setUpChocoBar(String text, int backgroundColor, int textColor, int icon)
    {

        Typeface montserrat = ResourcesCompat.getFont(VerifyPhone.this, R.font.montserrat);
        Typeface gilroyBold = ResourcesCompat.getFont(VerifyPhone.this, R.font.gilroy_extrabold);


        Snackbar snackbar = ChocoBar.builder().setBackgroundColor(getResources().getColor(backgroundColor))
                .setTextSize(15)
                .setTextColor(getResources().getColor(textColor))
                .setText(text)
                .setTextTypeface(montserrat)
                .setActionTextTypeface(gilroyBold)
                .setMaxLines(4)
                .setActionText("OK")

                .setActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setActionTextColor(getResources().getColor(textColor))
                .setActionTextSize(17)
                .setIcon(icon)
                .setActionTextTypefaceStyle(Typeface.BOLD)
                .setActivity(VerifyPhone.this)
                .setDuration(ChocoBar.LENGTH_LONG)

                .build();


        snackbar.show();


    }

    private void completeGoogleSignIn()
    {



        mFirebaseAuth.signInWithCredential(mAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            String email = user.getEmail();
                            if (prevActivity.equals(Constants.signup_Activity))
                            {
                                String uid = user.getUid();
                                CreateFirebaseUser createFirebaseUser = new CreateFirebaseUser(VerifyPhone.this, name, phoneNum, uid, onlyCcc, onlyNum, email, profilePicPosition);
                                createFirebaseUser.writeToDatabase();
                            }

                            else {

                                Intent intent = new Intent(VerifyPhone.this, MainActivity.class);
                                startActivity(intent);

                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }

    @Override
    public void accountSetUpFinished(boolean finished) {

        continueDialog.dismiss();

        if (finished)
        {
            Intent intent = new Intent(VerifyPhone.this, MainActivity.class);
            startActivity(intent);

        }

        else {

            final AlertDialog.Builder builder = new AlertDialog.Builder(VerifyPhone.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent;
                    if (prevActivity.equals(Constants.signup_Activity))
                    {
                        intent = new Intent(VerifyPhone.this, SignupActivity.class);
                    }
                    else {
                        intent = new Intent(VerifyPhone.this, LoginActivity.class);

                    }

                    startActivity(intent);
                }
            });
            builder.setTitle("Account Set-Up Failed");
            builder.setMessage("We were unable to process your Sign-Up Request, please try again later.");

            AlertDialog alertDialog = builder.create();

            alertDialog.show();

        }

    }
}